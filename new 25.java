public class BatchApexForInstallProduct implements Database.Batchable < sObject > {
	list<string>SliptedProducts = new list<string>();
	list<Product_Name__mdt> listproductMetaData = [SELECT Id, Location_based_Product_Names__c, Non_Location_Based_Product_Names__c FROM Product_Name__mdt];
	
	public Database.QueryLocator start(Database.BatchableContext BC) {
		return Database.getQueryLocator([SELECT Account__c, Id, Name, Location__c, Product__c, Serial_Lot_Number__c,
		Installed_Product_ID__c, Work_Order_Created__c, Work_Order_Completed_Date_Time__c, Status__c
			FROM Installed_Product__c where(Work_Order_Created__c = false AND Status__c = 'Operational') 
			OR (Work_Order_Created__c = True And Work_Order_Completed_Date_Time__c < TODAY) And Product__c 
		]);
	}
	public void execute(Database.BatchableContext BC, List < Installed_Product__c > newList){
		list < Work_Order__c > ListofWorkOrder = new list < Work_Order__c > ();
		list < Case > listOfCase = new list < Case > ();
		list < 	Work_Detail__c > ListofWorkDetail = new list < 	Work_Detail__c > ();
		
		Map < Id, list < Installed_Product__c >> InstallProductMap = new Map < ID, list < Installed_Product__c >> ();
		if (!newList.isEmpty()) {
			for (Installed_Product__c Is: newList) {
				if (InstallProductMap.containsKey(Is.Location__c)) {
					InstallProductMap.get(Is.Location__c).add(Is);
				} else {
					InstallProductMap.put(Is.Location__c, new list < Installed_Product__c > {Is});
				}
			}
		}
		

	}
	public void finish(Database.BatchableContext BC) {

	}
}