Global Class TestDiscountOnService 
{ 
     webservice static SVMXC.SFM_WrapperDef.SFM_PageData WSName(SVMXC.SFM_WrapperDef.SFM_TargetRecord request) 
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
         map<String,List<Sobject>> newDetailSobjectMap = new map<String, List<Sobject>>(); 
         detailSobjectMap = def.SFM_Page_GetDetailRecords(request, Allobj); 
         system.debug('detailSobjectMap = ' + detailSobjectMap); 
         // Loop through the map to get the list of Sobject records and process them 
         if(detailSobjectMap.size() > 0) 
         { 
              for(String str : detailSobjectMap.keyset()) 
               { 
                    list<SVMXC__Service_Order_Line__c> lstWoLines = new list<SVMXC__Service_Order_Line__c>(); 
                    lstWoLines = detailSobjectMap.get(str); 

                    if(lstWoLines.size() > 0) 
                    { 
                         for(Integer i = 0; i < lstWoLines.size(); i++) 
                         { 
                             if(lstWoLines[i].SVMXC__Activity_Type__c == 'Service') 
                             {
                                lstWoLines[i].SVMXC__Discount__c = 10; 
                             }
                         } 
                         update lstWoLines; 
                         newDetailSobjectMap.put(str, lstWoLines); 
                    } 
               }
         } 
         // Call the method below with the Header Sobject record and processed Detail Sobjects map 
         pagedata = def.SFM_Page_BuildResponse(request, headerSobj, newDetailSobjectMap); 
         system.debug('Response = ' + pagedata); 
         return pagedata;
    } 
}