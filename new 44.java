global class Assignment0610 {
 webservice static SVMXC.SFM_WrapperDef.SFM_PageData IPwDerror(SVMXC.SFM_WrapperDef.SFM_TargetRecord request) 
    { 
        
         // Describe all objects 
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
         SVMXC.SFM_ServicesDef def = new SVMXC.SFM_ServicesDef(); 

         // The method below returns the Header Sobject record 
         Sobject headerSobj = def.SFM_Page_GetHeaderRecord(request, Allobj); 

         // The method below returns the detail Sobject records in a map (Key: Tab Id, Value: List of Sobject records) 
         map<String,List<Sobject>> detailSobjectMap = new map<String, List<Sobject>>();
         detailSobjectMap = def.SFM_Page_GetDetailRecords(request, Allobj); 
         system.debug('detailSobjectMap = ' + detailSobjectMap); 
         // Loop through the map to get the list of Sobject records and process them 
         if(detailSobjectMap.size() > 0) 
         { 
              for(String str : detailSobjectMap.keyset()) 
               { 
                    list<SVMXC__Service_Order_Line__c> lstWoLines = new list<SVMXC__Service_Order_Line__c>(); 
                    lstWoLines = detailSobjectMap.get(str); 
                    if(lstWoLines.size() > 0){ 
                         for(Integer i = 0; i < lstWoLines.size()-1; i++){ 
                          for(Integer j = i+1; j < lstWoLines.size(); j++){ 
                             if(lstWoLines[j].Installed_Product__c ==lstWoLines[i].Installed_Product__c && lstWoLines[j].id!=lstWoLines[i].id  ) 
                             {
                 pagedata .response.message = 'Duplicate IP error'; 
                pagedata.response.success = false; 
                pagedata.response.messageType = 'SVMX_ERROR'; 
                return pagedata;
                             }
                           } 
                         }
                         pagedata.response.message = 'Saved Successfully'; 
                pagedata.response.success = true; 
                pagedata.response.messageType = 'SVMX_SUCCESS';  
                        
                    } 
               }
        }
         // Call the method below with the Header Sobject record and processed Detail Sobjects map 
         pagedata = def.SFM_Page_BuildResponse(request, headerSobj, detailSobjectMap); 
         system.debug('Response = ' + pagedata); 
         return pagedata;
    }
}