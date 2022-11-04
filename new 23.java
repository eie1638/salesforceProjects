public class SLAValidatio {
    public Static void CLKWarning (list<SVMXC__SVMX_Event__c> newlist){
        Set<Id> woId= new Set<Id>();
        for(SVMXC__SVMX_Event__c se : newList){
            if(se.SVMXC__Service_Order__c!=null){
                woId.add(se.SVMXC__Service_Order__c);
            }
        }
        Map<Id, SVMXC__Service_Order__c> wMap= new Map<Id,SVMXC__Service_Order__c>([Select Id, SVMXC__Onsite_Response_Customer_By__c From SVMXC__Service_Order__c Where ID In : woId]);
        for(SVMXC__SVMX_Event__c se : newList){
            if(se.SVMXC__Service_Order__c!=null && se.Acknowledge_SLA_Due_Date_Warning__c==false && se.SVMXC__StartDateTime__c!=null && wMap.get(se.SVMXC__Service_Order__c).SVMXC__Onsite_Response_Customer_By__c!=null){
                Datetime firstDate = wMap.get(se.SVMXC__Service_Order__c).SVMXC__Onsite_Response_Customer_By__c;
                Datetime secondDate = se.SVMXC__StartDateTime__c;
                if(secondDate > firstDate){
                    se.SVMXC__StartDateTime__c.addError('Time must be within Customer Onsite Response');
                }
            }
        }
    }
}