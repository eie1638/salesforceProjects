public class updatewd {
     public static void updateWDM(List<Parts_Stock_Detail__c> newList, Map<ID,Parts_Stock_Detail__c>oldMap){
        List<SVMXC__Service_Order_Line__c> wd = new List<SVMXC__Service_Order_Line__c>();
        for(Parts_Stock_Detail__c psd : newList){
            if(psd.Status__c!=null && psd.Status__c!=oldMap.get(psd.Id).Status__c && psd.Status__c=='Confirmed'){
               SVMXC__Service_Order_Line__c WLine = new SVMXC__Service_Order_Line__c();
                WLine.Id=psd.Work_Detail__c;
                WLine.Confirmed__c=true;
                wd.add(WLine);   
            }
        }
        if(!wd.isEmpty()){
         update wd;
        }
    }
}