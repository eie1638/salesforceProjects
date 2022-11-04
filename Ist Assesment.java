public class BatchApexForInstallProduct implements Database.Batchable < sObject >  {
	public Database.QueryLocator start(Database.BatchableContext BC) {
    list<string> SplitedProducts = new list<string>();
	list<Product_Name__mdt> listproductMetaData = [SELECT Id, Location_based_Product_Names__c, Non_Location_Based_Product_Names__c FROM Product_Name__mdt]; 
    for(Product_Name__mdt PNMD : listproductMetaData){
     string SplitedProducts1 =PNMD.Location_based_Product_Names__c + PNMD.Non_Location_Based_Product_Names__c;
     SplitedProducts = SplitedProducts1.split(',');
    }
     System.debug(SplitedProducts);
     
		return Database.getQueryLocator([SELECT Account__c, Id, Name, Location__c, Product__r.Name, Serial_Lot_Number__c,
		Installed_Product_ID__c, Work_Order_Created__c, Work_Order_Completed_Date_Time__c, Status__c
			FROM Installed_Product__c where(Work_Order_Created__c = false AND Status__c = 'Operational' And  Work_Order_Completed_Date_Time__c=null) 
			 OR (Work_Order_Created__c = True And Work_Order_Completed_Date_Time__c < TODAY And Product__r.Name IN :SplitedProducts) ]);
	}
	public void execute(Database.BatchableContext BC, List < Installed_Product__c > newList){
        
		list < Work_Order__c > ListofWorkOrder = new list < Work_Order__c >();
		list < Case > listOfCase = new list < Case > ();
		list < Work_Detail__c > ListofWorkDetail = new list <Work_Detail__c>(); 
        set<ID>AccId=new set<ID>();
	    Map < ID, list < Installed_Product__c >> InstallProductMap = new Map < ID, list < Installed_Product__c >> ();
		if (!newList.isEmpty()) {
			for (Installed_Product__c Is: newList) {                
				if (InstallProductMap.containsKey(Is.Location__c)) {
					InstallProductMap.get(Is.Location__c).add(Is);
				} else {
					InstallProductMap.put(Is.Location__c, new list < Installed_Product__c > {Is});
				}
			}
		} 
        
        for(ID locationID : InstallProductMap.keySet()){
            for(Installed_Product__c installedProduct1 :InstallProductMap.get(locationID) ){
                if(InstallProductMap.get(locationID).size()>0 ){
                    System.debug(installedProduct1);
                    //populate case and wo
                    Case newCase=new Case();
                      newCase.Installed_Product__c =installedProduct1.id;
                      newCase.Account__c=installedProduct1.Account__c;
                      newCase.Location__c =installedProduct1.Location__c;
                      newCase.Status ='New';
                      newCase.Origin ='Phone';
                     // newCase.Subject= newCase.Subject +''+installedProduct1.Account__r.Name+''+installedProduct1.Location__r.Name+''+installedProduct1.Serial_Lot_Number__c;              
                      listOfCase.add(newCase);
                      System.debug(listOfCase);
                }
                
            }
        }
          if(listOfCase.size()>0){
            insert listOfCase;     
        }
        
        Map<ID,ID> caseIDMap= new Map<ID,ID>();
        For(Case caseID:listOfCase)
        {
            caseIDMap.put(caseID.Location__c,caseID.Id);
        }
        
        for(ID locationID : InstallProductMap.keySet()){
            for(Installed_Product__c installedProduct1 :InstallProductMap.get(locationID) ){
                if(InstallProductMap.get(locationID).size()>0 ){
                    System.debug(installedProduct1);
                    //populate case and wo
                    Work_Order__c newWo = new Work_Order__c();
                      newWo.Case__c=caseIDMap.get(installedProduct1.Location__c);
                      newWo.Installed_Product__c = installedProduct1.Id;
                      newWo.Account__c=installedProduct1.Account__c;
                      newWo.Location__c =installedProduct1.Location__c;
                      newWo.Order_Status__c ='New';
                     // newWo.Subject__c= newCase.Subject +''+installedProduct1.Account__r.Name+''+installedProduct1.Location__r.Name+''+installedProduct1.Serial_Lot_Number__c;
                      ListofWorkOrder.add(newWo);
                     if(installedProduct1.Work_Order_Created__c==false){
                        installedProduct1.Work_Order_Created__c = true;
                        installedProduct1.Work_Order_Completed_Date_Time__c =null;
    
                    }
                   
                    }

                }
                
            }
        
        
        
        if(ListofWorkOrder.size()>0){
            insert ListofWorkOrder;     
        }
        map<ID , ID> workorderID = new map<ID , ID> ();
         For( Work_Order__c WoID : ListofWorkOrder)
        {
            workorderID.put(WoID.Location__c,WoID.Id);
        }
        
        for(ID locationID : InstallProductMap.keySet()){
            for(Installed_Product__c installedProduct1 :InstallProductMap.get(locationID) ){
                if(InstallProductMap.get(locationID).size()>1 ){
                    //populate wd
                    System.debug(installedProduct1);
                     Work_Detail__c wd = new Work_Detail__c(); 
                    
                }
                
            }
        }
        
        

	}
	public void finish(Database.BatchableContext BC) {
       
	}
}