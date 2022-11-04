public class bbtt2 {
    public static void method1(list<Work_Detail__c> newlist){
        set<id> WOIDs= new set<id>();
        for(Work_Detail__c wd:newlist){
            if(wd.Line_Type__c=='labor'){
                WOIDs.add(wd.WORK_ORDER__c);
            }
        }
        list<Work_Detail__c> AllWD=[SELECT Id, Line_Type__c, Start_Date_Time__c,Line_Status__c, End_Date_Time__c,
                                    WORK_ORDER__c FROM Work_Detail__c where WORK_ORDER__c in: WOIDs ];
        map<id,list<Work_Detail__c>> WOWDlist= new map<id,list<Work_Detail__c>>();
        
        for(Work_Detail__c wd: AllWD){
            if(wd.Line_Status__c=='labor' && wd.WORK_ORDER__c!=null && wd.Start_Date_Time__c!=null && wd.End_Date_Time__c!=null){
                if(WOWDlist.containskey(wd.WORK_ORDER__c)){
                    WOWDlist.get(wd.WORK_ORDER__c).add(wd);
                }
                else{
                    WOWDlist.put(wd.WORK_ORDER__c,new list<Work_Detail__c>{wd}) ;
                }
            }
        }
        
        for(Work_Detail__c wdc:newlist){
            if(wdc.Line_Status__c=='labor' && WOWDlist.get(wdc.WORK_ORDER__c)!=null){
                for(Work_Detail__c wda:WOWDlist.get(wdc.WORK_ORDER__c)){
                    if(wdc.Start_Date_Time__c==wda.Start_Date_Time__c &&
                    wdc.End_Date_Time__c==wda.End_Date_Time__c){
                        wdc.adderror('Overlapping Labor Lines found');
                    }
                    
                }
            }
            
        }
        
        
        
        
      
    } 
    
}