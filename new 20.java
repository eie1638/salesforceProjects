public class UpdateProductStock23 implements Database.Batchable<sObject> {
    public Database.QueryLocator start(Database.BatchableContext BC){
        return Database.getQueryLocator([Select Id,Location__c, Part__c,Processed__c,Qty__c,Status__c,Work_Detail__c From Parts_Stock_Detail__c Where Processed__c=False AND Status__c='Confirmed']);
    }
    public void execute(Database.BatchableContext BC, List<Parts_Stock_Detail__c>psdLst){
        Set<Id> wdId = new Set<Id>();
        for(Parts_Stock_Detail__c psd : psdLst){
            if(psd.Work_Detail__c!=null){
               wdId.add(psd.Work_Detail__c); 
            }
        }
        if(!wdId.isEmpty()){
          Map<Id,SVMXC__Service_Order_Line__c> wdMap= new Map<Id,SVMXC__Service_Order_Line__c>([Select Id,Confirmed__c From SVMXC__Service_Order_Line__c Where Id In:wdId]);
            Set<Id> LoId= new Set<Id>();
            List<Parts_Stock_Detail__c> psdSet = new List<Parts_Stock_Detail__c>();
            for(Parts_Stock_Detail__c psd : psdLst){
                if(wdMap.get(psd.Work_Detail__c).Confirmed__c==True && psd.Location__c!=null){
                    psdSet.add(psd);
                    LoId.add(psd.Location__c);
                }
            }
            if(!psdSet.isEmpty()){
                Map<Id,SVMXC__Site__c> locMap= new Map<Id,SVMXC__Site__c>([Select Id, Product_availability__c From SVMXC__Site__c Where ID IN:LoId]);
                List<SVMXC__Product_Stock__c> pslst= [Select ID,SVMXC__Status__c,SVMXC__Location__c,SVMXC__Quantity2__c,SVMXC__Product__c From SVMXC__Product_Stock__c Where SVMXC__Location__c IN: LoId];
                Map<Id, list<SVMXC__Product_Stock__c>> psMap= new Map<ID, list<SVMXC__Product_Stock__c>>();
                if(!pslst.isEmpty()){
                   for(SVMXC__Product_Stock__c ps : pslst){
                    if(psMap.containsKey(ps.SVMXC__Location__c)){
                        psMap.get(ps.SVMXC__Location__c).add(ps);
                    }
                    else{
                        psMap.put(ps.SVMXC__Location__c,new list<SVMXC__Product_Stock__c>{ps});
                    }
                } 
                }
                if(!psMap.isEmpty()){
                    List<SVMXC__Product_Stock__c> productStkLst= new List<SVMXC__Product_Stock__c>();
                    List<Parts_Stock_Detail__c> partStkLst= new List<Parts_Stock_Detail__c>();
                    for(Parts_Stock_Detail__c psd : psdSet){
                        if(psMap.containsKey(psd.Location__c) && psMap.get(psd.Location__c)!=null){
                            for(SVMXC__Product_Stock__c ps: psMap.get(psd.Location__c)){
                                if(ps.SVMXC__Location__c==psd.Location__c && ps.SVMXC__Product__c==psd.Part__c && ps.SVMXC__Status__c=='Available' && ps.SVMXC__Quantity2__c>=psd.Qty__c){
                                    ps.SVMXC__Quantity2__c=ps.SVMXC__Quantity2__c-psd.Qty__c;
                                    productStkLst.add(ps);
                                    psd.Processed__c=true;
                                    partStkLst.add(psd);
                                    locMap.get(psd.Location__c).Product_availability__c='available';
                                }
                                else{
                                    psd.Processed__c=true;
                                    psd.Status__c='Out Of Stock';
                                    partStkLst.add(psd);
                                    locMap.get(psd.Location__c).Product_availability__c='Out Of stock';
                                }
                            }
                        }
                        else{
                            psd.Processed__c=true;
                            psd.Status__c='Out Of Stock';
                            partStkLst.add(psd);
                            locMap.get(psd.Location__c).Product_availability__c='Out Of stock';
                        }
                        
                    }
                    if(!partStkLst.isEmpty()){
                        update partStkLst;
                    }
                    if(!productStkLst.isEmpty()){
                        update productStkLst;
                    }
                    if(!locMap.isEmpty()){
                        update locMap.values();
                    }
                }
            }
            
        }
    }
    public void finish(Database.BatchableContext BC){
        
    }
}