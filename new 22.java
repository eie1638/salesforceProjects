public class BatchApexIPL implements Database.Batchable<sObject> {
     public Database.QueryLocator start(Database.BatchableContext BC){
        return Database.getQueryLocator([Select Id, Parts_Order__c,Error_message__c,In_Error__c,Processed__c From Inventory_Process_Log__c Where Processed__c=False]);
    }
      public void execute(Database.BatchableContext BC, List<Inventory_Process_Log__c>iplist){
        Set<Id> SetpoId= new Set<Id>();
        for(Inventory_Process_Log__c ipl : iplist){
            if(ipl.Parts_Order__c!=null){
                SetpoId.add(ipl.Parts_Order__c);
            }
        }
        if(!SetpoId.isEmpty()){
        List<Inventory_Process_Log__c> ipsLst= new List<Inventory_Process_Log__c>();
        List<SVMXC__Product_Stock__c> psList= new List<SVMXC__Product_Stock__c>();
        List<SVMXC__RMA_Shipment_Line__c> poMap=[Select Id,SVMXC__Product__c,from_Location__c,Qty__c,SVMXC__RMA_Shipment_Order__c From SVMXC__RMA_Shipment_Line__c Where SVMXC__RMA_Shipment_Order__c IN:SetpoId ];
        Map<Id,list<SVMXC__RMA_Shipment_Line__c>>plMap = new Map<Id,list<SVMXC__RMA_Shipment_Line__c>>();
        for(SVMXC__RMA_Shipment_Line__c po : poMap){
            if(plMap.containsKey(po.SVMXC__RMA_Shipment_Order__c)){
                 .get(po.SVMXC__RMA_Shipment_Order__c).add(po);
            }
            else{
                plMap.put(po.SVMXC__RMA_Shipment_Order__c, new list<SVMXC__RMA_Shipment_Line__c>{po});
            }
        }
        for(Inventory_Process_Log__c IPL : iplist){
            if(IPL.Parts_Order__c!=null){
                if(!plMap.get(IPL.Parts_Order__c).isEmpty()){
                for(SVMXC__RMA_Shipment_Line__c pl : plMap.get(IPL.Parts_Order__c)){
                    if(pl.SVMXC__Product__c!=null && pl.From_Location__c!=null && pl.Qty__c!=null){
                        SVMXC__Product_Stock__c ps= new SVMXC__Product_Stock__c();
                        ps.SVMXC__Location__c=pl.from_Location__c;
                        ps.SVMXC__Quantity2__c=pl.Qty__c;
                        ps.SVMXC__Status__c='Available';
                        ps.SVMXC__Product__c=pl.SVMXC__Product__c;
                        psList.add(ps);
                        ipl.Processed__c=true;
                        ipsLst.add(ipl);
                    }
                    else if(pl.SVMXC__Product__c!=null && pl.From_Location__c!=null && pl.Qty__c==null){
                        ipl.In_Error__c=true;
                        ipl.Error_message__c='no line Qty found';
                        ipl.Processed__c=true;
                        ipsLst.add(ipl);
                        break;
                    }
                    else if(pl.SVMXC__Product__c!=null && pl.From_Location__c==null && pl.Qty__c!=null){
                        ipl.In_Error__c=true;
                        ipl.Error_message__c='no location info found ';
                        ipl.Processed__c=true;
                        ipsLst.add(ipl);
                        break;
                    }
                }
            }
            else{
                ipl.Processed__c=true;
                ipl.In_Error__c=true;
                ipl.Error_message__c='no line found';
                ipsLst.add(ipl);
            }
            }
        }
        if(!ipsLst.isEmpty()){
            update ipsLst;
        }
        if(!psList.isEmpty()){
            Insert psList;
        }
        }
    }
    public void finish(Database.BatchableContext BC){
    }
}