public class WDclassForUpdate {
    public static void methodWDReq(List<Work_Detail__c>newList){
        for(Work_Detail__c WD :newList ){
            if(WD.Line_Type__c=='labor' && (WD.Start_Date_Time__c==null ||WD.End_Date_Time__c==null)){
                WD.addError('Start date and end date cant be null');
            }
            
        }
}
    public static void methodWDoverlapping(List<Work_Detail__c>newList){
        
        //Time Overlap check between Work Detail Lines of Same Related Work Order
        set<id>WdId = new set<id>();
        map<id , list<Work_Detail__c>>AllWDrelatedToWo = new map<id , list<Work_Detail__c>>();
        for(Work_Detail__c wd :newList){
            if(wd.Work_Order__c !=null && wd.Line_Type__c=='Labor'){
                WdId.add(wd.Work_Order__c);
                
            } 
        }
        List<Work_Detail__c> wdLValue= [Select Id,Line_Type__c, Line_Status__c,Start_Date_Time__c,End_Date_Time__c, Work_Order__c From Work_Detail__c Where Work_Order__c IN: WdId And Line_Type__c='Labor' ];
         if(!wdLValue.isEmpty()){
                for(Work_Detail__c wd : newList){
                    if(wd.Line_Type__c=='Labor'){
                       for(Work_Detail__c wod : wdLValue){
                        if(wd.Work_Order__c==wod.Work_Order__c && wd.Id!=wod.Id && AllWDrelatedToWo.containsKey(wd.Work_Order__c)){
                            AllWDrelatedToWo.get(wd.Work_Order__c).add(wod);
                        }
                        else if(wd.Work_Order__c==wod.Work_Order__c && wd.Id!=wod.Id){
                            AllWDrelatedToWo.put(wd.Work_Order__c,new List<Work_Detail__c>{wod});
                        }
                     } 
                    }
                    
                }
         }
                if(!newList.isEmpty()){
                    for(Work_Detail__c wd : newList){
                        if(wd.Line_Type__c== 'Labor' && AllWDrelatedToWo.containsKey(wd.Work_Order__c)  && AllWDrelatedToWo.get(wd.Work_Order__c)!=null){
                             for(Work_Detail__c wod : AllWDrelatedToWo.get(wd.Work_Order__c)){
                            if(wd.Start_Date_Time__c < wod.End_Date_Time__c){
                                wd.addError('Time Overlapping');
                              }
                          }
                        }  
                    }
            }
       
    }

}