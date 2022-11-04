global class BBt implements Database.Batchable<sObject>{
    
    global Database.QueryLocator start(Database.BatchableContext BC) {
        String status = 'Operational';
        String query = 'SELECT Id,SVMXC__Product__r.name,SVMXC__Company__c,SVMXC__Site__c,SVMXC__Company__r.name,SVMXC__Site__r.name,SVMXC__Installed_Product__c.SVMXC__Serial_Lot_Number__c,Work_Order_Created__c,Work_Order_Completed_Date_Time__c from SVMXC__Installed_Product__c where SVMXC__Status__c =:status AND ((Work_Order_Created__c = false AND Work_Order_Completed_Date_Time__c  = null) OR (Work_Order_Created__c = True AND Work_Order_Completed_Date_Time__c < TODAY))';
        return Database.getQueryLocator(query);
    }
    
    global void execute(Database.BatchableContext BC, List<SVMXC__Installed_Product__c> scope) 
    {
        List<String> locationProductList = new List<String>();
        List<String> nonLocationProductList = new List<String>();
        Product_Names__mdt[] productNames = [SELECT MasterLabel,Location_based_Product_Names__c,Non_Location_Based_Product_Names__c FROM Product_Names__mdt];
        for (Product_Names__mdt pn : productNames)
        {
            if(pn.Location_based_Product_Names__c != null)
                locationProductList.add(pn.Location_based_Product_Names__c);
            
            if(pn.Non_Location_Based_Product_Names__c != null)
                nonLocationProductList.add(pn.Non_Location_Based_Product_Names__c);
            
        }
        
        List<SVMXC__Installed_Product__c> locationIps = new List<SVMXC__Installed_Product__c>();
        List<SVMXC__Installed_Product__c> nonLocationIps = new List<SVMXC__Installed_Product__c>();
        for(SVMXC__Installed_Product__c ip : scope)
        {
            if(locationProductList.contains(ip.SVMXC__Product__r.name))
                locationIps.add(ip);
            if(nonLocationProductList.contains(ip.SVMXC__Product__r.name))
                nonLocationIps.add(ip);
        }
        
        List<SVMXC__Installed_Product__c> updatedIps = new List<SVMXC__Installed_Product__c>();
        
        //Location based
        Set<Id> locSet = new Set<Id>();
        Map<Id,List< SVMXC__Installed_Product__c>> locIpMap = new Map<Id,List< SVMXC__Installed_Product__c>>();
        for(SVMXC__Installed_Product__c ip : locationIps)
        {
            if(ip.SVMXC__Site__c  != null)
                locSet.add(ip.SVMXC__Site__c);
            if(!locIpMap.containsKey(ip.SVMXC__Site__c ))
                locIpMap.put(ip.SVMXC__Site__c ,new List<SVMXC__Installed_Product__c >());
            locIpMap.get(ip.SVMXC__Site__c ).add(ip);
        }
        
        List<Id> locAccSet = new List<Id>();
        Map<Id,List< SVMXC__Site__c >> locMap = new Map<Id,List< SVMXC__Site__c >>();
        List<SVMXC__Site__c> locList = [SELECT Id,Name,SVMXC__Account__c,SVMXC__Account__r.Name FROM SVMXC__Site__c WHERE Id IN:locSet];
        for(SVMXC__Site__c l : locList)
        {
            if(l.SVMXC__Account__c != null)
                locAccSet.add(l.SVMXC__Account__c);
            if(!locMap.containsKey(l.SVMXC__Account__c ))
                locMap.put(l.SVMXC__Account__c ,new List<SVMXC__Site__c >());
            locMap.get(l.SVMXC__Account__c).add(l);
        }
        
        List<Case> locationCases = new List<Case>();
        Map<Id,Integer> ipnumberMap = new Map<Id,Integer>();
        for(Id a : locAccSet)
        {
            if(locMap.containsKey(a) && locMap.get(a)!=null)
            {
                for(SVMXC__Site__c  l : locMap.get(a))
                {
                    if(locIpMap.containsKey(l.Id) && locIpMap.get(l.Id)!=null)
                    {
                        Case c = new Case();
                        c.Status = 'New';
                        c.Origin = 'Phone';
                        c.Priority = 'High';
                        c.AccountId = a;
                        c.SVMXC__Site__c = l.Id;
                        c.Installed_Product__c = locIpMap.get(l.Id)[0].Id;
                        c.Subject = l.SVMXC__Account__r.Name+' '+l.Name+' '+locIpMap.get(l.Id)[0].SVMXC__Serial_Lot_Number__c;
                        locationCases.add(c);  
                        ipnumberMap.put(l.Id,locIpMap.get(l.Id).size());
                        if(locIpMap.get(l.Id)[0].Work_Order_Created__c == false)
                            locIpMap.get(l.Id)[0].Work_Order_Created__c = true;
                        if(locIpMap.get(l.Id)[0].Work_Order_Completed_Date_Time__c != null)
                            locIpMap.get(l.Id)[0].Work_Order_Completed_Date_Time__c = null;
                        updatedIps.add(locIpMap.get(l.Id)[0]);
                    }
                }
            }
        }
        if(locationCases!=null && locationCases.size()>0)
            insert locationCases;
        
        List<SVMXC__Service_Order__c> woList = new List<SVMXC__Service_Order__c>();
        for(Case c:locationCases)
        {
            SVMXC__Service_Order__c wo = new SVMXC__Service_Order__c ();
            wo.SVMXC__Company__c = c.AccountId ;
            wo.Installed_Product__c = c.Installed_Product__c;
            wo.SVMXC__Site__c = c.SVMXC__Site__c;
            wo.Subject__c = c.Subject;
            wo.Location_Based__c = true;
            wo.SVMXC__Order_Status__c = 'New';
            wo.SVMXC__Case__c = c.Id;
            woList.add(wo);
        }
        if(woList!=null && woList.size()>0)
            insert woList;
        
        List<SVMXC__Service_Order_Line__c> wdList = new List<SVMXC__Service_Order_Line__c>();
        Id recId = Schema.SObjectType.SVMXC__Service_Order_Line__c.getRecordTypeInfosByName().get('Usage/Consumption').getRecordTypeId();
        for(SVMXC__Service_Order__c wo : woList)
        {
            if(locIpMap.containsKey(wo.SVMXC__Site__c) && locIpMap.get(wo.SVMXC__Site__c)!=null)
            {
                for(SVMXC__Installed_Product__c ip : locIpMap.get(wo.SVMXC__Site__c))
                {
                    SVMXC__Service_Order_Line__c wd = new 	SVMXC__Service_Order_Line__c ();
                    wd.SVMXC__Line_Type__c = 'Installed Product';
                    wd.Installed_Product__c = ip.Id;
                    wd.RecordTypeId = recId;
                    wd.SVMXC__Line_Status__c = 'Open';
                    wd.SVMXC__Service_Order__c  = wo.Id;
                    wd.SVMXC__Actual_Quantity2__c = ipnumberMap.get(wo.SVMXC__Site__c);
                    wdList.add(wd);
                }
            }
        }
        if(wdList!=null && wdList.size()>0)
            insert wdList;
        
        //Non-Location based
        Set<Id> nonlocSet = new Set<Id>();
        Map<Id,List<SVMXC__Installed_Product__c>> nonlocIpMap = new Map<Id,List<SVMXC__Installed_Product__c>>();
        for(SVMXC__Installed_Product__c ip : nonLocationIps)
        {
            if(ip.SVMXC__Site__c  != null)
            {
                nonlocSet.add(ip.SVMXC__Site__c);
                if(!nonlocIpMap.containsKey(ip.SVMXC__Site__c ))
                    nonlocIpMap.put(ip.SVMXC__Site__c ,new List<SVMXC__Installed_Product__c >());
                nonlocIpMap.get(ip.SVMXC__Site__c ).add(ip);
            }
        }
        
        List<Id> nonlocAccSet = new List<Id>();
        Map<Id,List< SVMXC__Site__c >> nonlocMap = new Map<Id,List< SVMXC__Site__c >>();
        List<SVMXC__Site__c> nonlocList = [SELECT Id,Name,SVMXC__Account__c,SVMXC__Account__r.Name FROM SVMXC__Site__c WHERE Id IN:nonlocSet];
        for(SVMXC__Site__c l : nonlocList)
        {
            if(l.SVMXC__Account__c != null)
                nonlocAccSet.add(l.SVMXC__Account__c);
            if(!nonlocMap.containsKey(l.SVMXC__Account__c ))
                nonlocMap.put(l.SVMXC__Account__c ,new List<SVMXC__Site__c >());
            nonlocMap.get(l.SVMXC__Account__c).add(l);
        }
        
        List<Case> nonLocationCases = new List<Case>();
        for(Id a : nonlocAccSet)
        {
            if(nonlocMap.containsKey(a) && nonlocMap.get(a)!=null)
            {
                for(SVMXC__Site__c  l : nonlocMap.get(a))
                {
                    if(nonlocIpMap.containsKey(l.Id) && nonlocIpMap.get(l.Id)!=null)
                    {
                        for(SVMXC__Installed_Product__c ip : nonlocIpMap.get(l.Id))
                        {
                            Case c = new Case();
                            c.Status = 'New';
                            c.Origin = 'Phone';
                            c.Priority = 'High';
                            c.AccountId = a;
                            c.SVMXC__Site__c = l.Id;
                            c.Installed_Product__c = ip.Id;
                            c.Subject = l.SVMXC__Account__r.Name+' '+l.Name+' '+ip.SVMXC__Serial_Lot_Number__c;
                            nonLocationCases.add(c);  
                            ipnumberMap.put(l.Id,nonlocIpMap.get(l.Id).size());
                            if(ip.Work_Order_Created__c == false)
                                ip.Work_Order_Created__c = true;
                            if(ip.Work_Order_Completed_Date_Time__c != null)
                                ip.Work_Order_Completed_Date_Time__c = null;
                            updatedIps.add(ip);
                        }
                    }
                }
            }
        }
        if(nonLocationCases!=null && nonLocationCases.size()>0)
            insert nonLocationCases;
        
        List<SVMXC__Service_Order__c> nonLocationWOList = new List<SVMXC__Service_Order__c>();
        for(Case c:nonLocationCases)
        {
            SVMXC__Service_Order__c wo = new SVMXC__Service_Order__c ();
            wo.SVMXC__Company__c = c.AccountId;
            wo.Installed_Product__c = c.Installed_Product__c;
            wo.SVMXC__Site__c = c.SVMXC__Site__c;
            wo.Subject__c = c.Subject;
            wo.SVMXC__Order_Status__c = 'New';
            wo.SVMXC__Case__c = c.Id;
            nonLocationWOList.add(wo);
        }
        if(nonLocationWOList!=null && nonLocationWOList.size()>0)
            insert nonLocationWOList;
        
        if(updatedIps!=null && updatedIps.size()>0)
            update updatedIps;
    }   
    global void finish(Database.BatchableContext BC) {
    }
}