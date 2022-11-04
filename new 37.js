Global Class TestWorkOrderStatus 
{ 
    webservice static SVMXC.SFM_WrapperDef.SFM_PageData StatusWorkOrder(SVMXC.SFM_WrapperDef.SFM_TargetRecord request) 
    { 
        system.debug('Request = ' + request);
        SVMXC.SFM_WrapperDef.SFM_PageData pagedata = new SVMXC.SFM_WrapperDef.SFM_PageData(); 
        map<String,Schema.SObjectType> Allobj = new map<String, Schema.SObjectType>(); 
        map<String, Schema.SObjectType> gd = new Map<String, Schema.SObjectType>(); 
        gd = Schema.getGlobalDescribe(); 
        if(gd.size() > 0) 
        { 
            for(Schema.SObjectType s : gd.values()) 
            { 
               Schema.DescribeSObjectResult result = s.getDescribe(); 
               Allobj.put(result.getName(), s); 
            } 
        } 
		system.debug(Allobj);
        SVMXC.SFM_ServicesDef def = new SVMXC.SFM_ServicesDef(); 
        Sobject headerSobj = def.SFM_Page_GetHeaderRecord(request, Allobj); 
        SVMXC__Service_Order__c objWO = new SVMXC__Service_Order__c(); 
        objWO = (SVMXC__Service_Order__c) headerSobj; 
        if(objWO.Customer_Feedback_Captured__c == true && objWO.Customer_Rating__c!=null) 
        { 
            objWO.Status__c = 'Accepted'; 
            
            try 
            { 
                update objWO; 
                pagedata.response.message = 'Saved Successfully'; 
                pagedata.response.success = true; 
                pagedata.response.messageType = 'SVMX_SUCCESS'; 
            } 
            catch(Exception ex) 
            { 
                pagedata .response.message = ex.getmessage(); 
                pagedata.response.success = false; 
                pagedata.response.messageType = 'SVMX_ERROR'; 
                return pagedata; 
             } 
        } else{
			 objWO.Status__c = 'Rejected'; 
            
            try 
            { 
                update objWO; 
                pagedata.response.message = 'Saved Successfully'; 
                pagedata.response.success = true; 
                pagedata.response.messageType = 'SVMX_SUCCESS'; 
            } 
            catch(Exception ex) 
            { 
                pagedata .response.message = ex.getmessage(); 
                pagedata.response.success = false; 
                pagedata.response.messageType = 'SVMX_ERROR'; 
                return pagedata; 
             } 
		}
        system.debug('objWO = ' + objWO); 
        //The method below returns the detail Sobject records in a map (Key: Tab Id, Value: List of Sobject records)
        map<String,List<Sobject>> detailSobjectMap = new map<String, List<Sobject>>(); 
        detailSobjectMap = def.SFM_Page_GetDetailRecords(request, Allobj); 
        // Call the method below with the processed Header Sobject record and Detail Sobjects map 
        pagedata = def.SFM_Page_BuildResponse(request, objWO, detailSobjectMap); 
        system.debug('Response = ' + pagedata); 
        return pagedata; 
    } 
}