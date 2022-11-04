list<string> SliptedProducts = new list<string>();
	list<Product_Name__mdt> listproductMetaData = [SELECT Id, Location_based_Product_Names__c, Non_Location_Based_Product_Names__c FROM Product_Name__mdt]; 
    for(Product_Name__mdt PNMD : listproductMetaData){
     string SliptedProducts1 =PNMD.Location_based_Product_Names__c + PNMD.Non_Location_Based_Product_Names__c;
        SliptedProducts = SliptedProducts1.split(',');
    }
System.debug(SliptedProducts[0]);



 1 location multi ip 
 1 account multi ip 
 