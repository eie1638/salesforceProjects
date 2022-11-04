public class PartSwapHandler {
    public static void oldIpDeinstall(List<Part_Swap__c> newList){
        
        
        List<SVMXC__Installed_Product__c> oldIpToDeinstall = new List<SVMXC__Installed_Product__c>();
        List<SVMXC__Installed_Product__c> newIpToBeInserted = new List<SVMXC__Installed_Product__c>();
        
        Map<ID, String> mapOfIpSerialNumber = new Map<ID, String>();
        Map<String, List<SVMXC__Service_Contract_Products__c>> mapOfCoveredProduct = new Map<String, List<SVMXC__Service_Contract_Products__c>>();
        Map<String, List<SVMXC__Warranty__c>> mapOfProductWarranty = new Map<String, List<SVMXC__Warranty__c>>();
        
        List<SVMXC__Warranty__c> productWarranty = new List<SVMXC__Warranty__c>();
        List<SVMXC__Service_Contract_Products__c> coveredProduct = new List<SVMXC__Service_Contract_Products__c>();
        
        Set<ID> oldIpId = new Set<ID>();
        
        for(Part_Swap__c eachPart : newList){
            oldIpId.add(eachPart.Swapped_Installed_Product__c); 
        }
         //coveredProduct relation R00N70000001hzdkEAA__r and product warranty relation SVMXC__Warranty__r
        for(SVMXC__Installed_Product__c  eachIp : [SELECT ID, SVMXC__Status__c,SVMXC__Serial_Lot_Number__c,
                                                   (SELECT ID, SVMXC__End_Date__c, SVMXC__Installed_Product__c FROM SVMXC__R00N70000001hzdkEAA__r
                                                    WHERE SVMXC__End_Date__c > Today),
                                                   (SELECT ID, SVMXC__End_Date__c, SVMXC__Installed_Product__c FROM SVMXC__Warranty__r 
                                                    WHERE SVMXC__End_Date__c > Today)
                                                    FROM SVMXC__Installed_Product__c WHERE ID IN: oldIpId])
            
        {
            eachIp.SVMXC__Status__c = 'Deinstalled';
            System.debug('oldIp list size '+eachIp);
            oldIpToDeinstall.add(eachIp);
            mapOfIpSerialNumber.put(eachIp.ID,eachIp.SVMXC__Serial_Lot_Number__c);
            System.debug('Serial number of old ip'+mapOfIpSerialNumber);
            if(eachIp.SVMXC__R00N70000001hzdkEAA__r.size()>0){
                mapOfCoveredProduct.put(eachIp.SVMXC__Serial_Lot_Number__c,eachIp.SVMXC__R00N70000001hzdkEAA__r);
                System.debug('covered product of old ip'+mapOfCoveredProduct);
            }
            
            if(eachIp.SVMXC__Warranty__r.size()>0){
                mapOfProductWarranty.put(eachIp.SVMXC__Serial_Lot_Number__c,eachIp.SVMXC__Warranty__r);
                System.debug('product warranty of old ip'+mapOfProductWarranty);
            }
            
        }
        
        for(Part_Swap__c eachPart : newList ){
            SVMXC__Installed_Product__c newIp = new SVMXC__Installed_Product__c();
            newIp.Name = 'PartSwapInstallProduct'+DateTime.now();
            newIp.SVMXC__Serial_Lot_Number__c = mapOfIpSerialNumber.get(eachPart.Swapped_Installed_Product__c);
            newIp.SVMXC__Product__c = eachPart.Part__c;
            newIP.SVMXC__Status__c = 'Installed';
            newIP.SVMXC__Date_Installed__c = Date.Today();
            newIp.SVMXC__Company__c = eachPart.Account__c;
            newIP.SVMXC__Site__c = eachPart.Repair_Location__c;
            newIpToBeInserted.add(newIp);
            
        }
        
        if(!oldIpToDeinstall.isEmpty()){
            update oldIpToDeinstall;
        }
        
        if(!newIpToBeInserted.isEmpty()){
            insert newIpToBeInserted;
        }
        System.debug('new ip list'+newIpToBeInserted); 
               
        for(SVMXC__Installed_Product__c eachNewIp : newIpToBeInserted){
            System.debug('iterarting on newly inserted ip loop');
            if(mapOfCoveredProduct.containsKey(eachNewIp.SVMXC__Serial_Lot_Number__c)){
                List<SVMXC__Service_Contract_Products__c> coveredProd = mapOfCoveredProduct.get(eachNewIp.SVMXC__Serial_Lot_Number__c);
                for(SVMXC__Service_Contract_Products__c eachCoveredProd : coveredProd){
                    eachCoveredProd.SVMXC__Installed_Product__c = eachNewIp.Id; 
                    coveredProduct.add(eachCoveredProd);
                    System.debug('Covered product list size'+eachCoveredProd);
                }
            }
            
            if(mapOfProductWarranty.containsKey(eachNewIp.SVMXC__Serial_Lot_Number__c)){
                List<SVMXC__Warranty__c> prodWarranty = mapOfProductWarranty.get(eachNewIp.SVMXC__Serial_Lot_Number__c);
                for(SVMXC__Warranty__c eachProductWarranty : prodWarranty){
                    eachProductWarranty.SVMXC__Installed_Product__c = eachNewIp.Id;
                    productWarranty.add(eachProductWarranty);
                    system.debug('product warranty list size'+eachProductWarranty);
                    
                }
            }
              
        }
        
        if(!coveredProduct.isEmpty()){
            update coveredProduct;
        }
        System.debug('coveredProduct'+coveredProduct.size());
        if(!productWarranty.isEmpty()){
            update productWarranty;
        }
        system.debug('productWarranty'+productWarranty.size());
  
   }
} 