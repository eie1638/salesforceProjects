public class SupportCase {
    public static void supportCase(List<Case> clst){
        Set<Id> cId= new Set<Id>();
        Set<String> sId= new Set<String>();
        for(Case c : clst){
            if(c.AccountId!=null && c.Origin!=null){
                cId.add(c.AccountId);
                sId.add(c.Origin);
            }
        }
        if(!cId.isEmpty()){
            List<Case> cList=[Select Id, AccountId,Origin, ParentId From Case Where AccountId IN:cId AND Origin IN:sId];
            Map<Id,Case> cMap= new Map<Id,Case>();
            if(!cList.isEmpty()){
                for(Case c : cList){
                    if(c.AccountId!=null && c.ParentId==null && !cMap.containsKey(c.AccountId)){
                        cMap.put(c.AccountId,c);
                    }
                }
            }
            for(Case c : clst){
                if(c.AccountId!=null && cMap.containsKey(c.AccountId) && cMap.get(c.AccountId).Origin==c.Origin) {
                    c.ParentId= cMap.get(c.AccountId).Id;
                    if(c.Priority=='High'){
                        c.SVMXC__Perform_Auto_Entitlement__c=True;
                    }
                }
            }
            
        }
    }
}