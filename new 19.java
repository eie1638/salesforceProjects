public class productstockBatch implements Database.Batchable<sObject> , Schedulable {
    public Database.QueryLocator start(Database.BatchableContext BC){
        return Database.getQueryLocator([SELECT ID, Name ,Work_Detail__r.Confirmed__c,Location__c,Part__c,Qty__c,Status__c,Processed__c FROM Parts_Stock_Detail__c 
                                         WHERE Status__c = 'Confirmed' AND Processed__c = false AND Work_Detail__r.Confirmed__c = true ]);
    }
    public void execute(Database.BatchableContext BC, List<Parts_Stock_Detail__c>partStockDetail){
        Map<String,List<Parts_Stock_Detail__c>> partStockMap = new Map<String,List<Parts_Stock_Detail__c>>();
        Set<Id> locSet = new Set<Id>();
        Set<Id> partSet = new Set<Id>();
        for(Parts_Stock_Detail__c psd :partStockDetail ){         
            if(!partStockMap.containsKey((string)psd.Location__c)){
                List<Parts_Stock_Detail__c> listObj = new List<Parts_Stock_Detail__c>();
                listObj.add(psd);
                partStockMap.put((String)psd.Location__c,listObj);
            }
            else
            {
                partStockMap.get((String)psd.Location__c).add(psd);
            } 
            locSet.add(psd.Location__c);
            partSet.add(psd.Part__c);
        }
        List<SVMXC__Product_Stock__c> prdtStockUpdate = new List<SVMXC__Product_Stock__c>();
        List<Parts_Stock_Detail__c> partStockDetailUpdate = new List<Parts_Stock_Detail__c>();
        List<Parts_Stock_Detail__c> partStockDetailUpdate1 = new List<Parts_Stock_Detail__c>();
        
        List<SVMXC__Product_Stock__c> productStockList = [SELECT ID, SVMXC__Location__c,SVMXC__Quantity2__c,SVMXC__Product__c
                                                FROM SVMXC__Product_Stock__c WHERE SVMXC__Location__c In:locSet AND
                                                SVMXC__Product__c IN : partSet AND SVMXC__Status__c ='Available'];
        
        
        if(productStockList.size()>0){
        for(SVMXC__Product_Stock__c ps:productStockList){
            List<Parts_Stock_Detail__c> partsList = partStockMap.get((String)ps.SVMXC__Location__c);
            for (Parts_Stock_Detail__c partStock :partsList){
                if(ps.SVMXC__Quantity2__c < partStock.Qty__c) {
                    partStock.Status__c='Out Of Stock';
                    partStock.Processed__c=true;
                    partStockDetailUpdate.add(partStock);
                     
                } 
                else{
                    ps.SVMXC__Quantity2__c= ps.SVMXC__Quantity2__c - partStock.Qty__c;
                        partStock.Processed__c= true;
                        partStockDetailUpdate1.add(partStock);
                        prdtStockUpdate.add(ps);
                }
             }
        }
           
            if(!partStockDetailUpdate1.isEmpty()){
                update partStockDetailUpdate1;
            }
            if(!partStockDetailUpdate.isEmpty()){
               update partStockDetailUpdate; 
            }
            if(!prdtStockUpdate.isEmpty()){
                update prdtStockUpdate;
            } 
            
        }
        else{        
            for(Parts_Stock_Detail__c psd :partStockDetail ){
                psd.Status__c='Out Of Stock';
                psd.Processed__c=true;
                partStockDetailUpdate.add(psd);
            }
            if(!partStockDetailUpdate.isEmpty()){
                update partStockDetailUpdate;
            }           
        }      
    }
    public void finish(Database.BatchableContext BC){
        
    }
    public void execute(SchedulableContext SC){
        productstockBatch psb = new productstockBatch();
        Database.executeBatch(psb);
          
/*      for(Integer i = 0; i < 60 ; i += 2){
        String cronTrigger = '0 '+i+' * * * ?';
        System.schedule(' Run in every 2 minute'+i,cronTrigger,new productstockBatch());
*/
}
}