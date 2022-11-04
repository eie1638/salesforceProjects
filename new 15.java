public class updateCaseFromAccClass {
    public static void updateCaseFromAccMethod(list<Case>newList){
        set<id>setId=new set<id>();
        set<id> oriID = new set<id>();
        for(Case c :newList){
            if(c.AccountId!=null && c.Origin !=null){
                setId.add(c.AccountId);
                oriID.add(c.Origin);
            }
        }
        map<id , list<case>>mapcase = new map<id , list<case>>();
        List<Case> existCase=[SELECT id, Origin,AccountId
                              FROM Case WHERE AccountId IN :setId
                              And Origin IN :oriID];
         for(Case mapstore: existCase)
        {
            if(mapcase.keyset().contains(mapstore.AccountId)){
                mapcase.get(mapstore.AccountId).add(mapstore);
            }
            else{
                mapcase.put(mapstore.AccountId,new list<case>{mapstore});
            }
        }
     for(Case newCase: newList){
            if(mapcase.containskey(newCase.AccountId)){
                for(case c :mapcase.get(newCase.AccountId)){
                    if(newCase.Origin==c.Origin){
                        newCase.ParentId=c.id;
                    }
                }
            }
        }
    }
 }