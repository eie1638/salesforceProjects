/************
Test Class for SMAX_PS_GetStorageBin
************/
@isTest
public class SMAX_PS_GetStorageBin_UT {
 
    @isTest(SeeAllData=true)
    public static void populateStorageBin(){
        BusinessUnit__c bu = createBusinessUnit(true);
        
        Account acc= new Account();
        acc.Name = String.valueOf(System.currentTimeMillis()).right(6);
        acc.Smx_code__c = String.valueOf(System.currentTimeMillis()).right(6);
        acc.Smx_FormalAccountName__c = 'test'; 
        acc.Smx_FormalAccountNameKana__c = 'テスト';
        acc.Smx_MasterDivision__c = '0';
        acc.YomiName__c = 'テスト';
        acc.Smx_YomiNameKanaHalf__c = 'ﾃｽﾄ';
        acc.smx_segment__c = '0';
        acc.BillingPostalCode = '100-0000';
        acc.BillingState = '東京都';
        acc.BillingCity = '千代田区千代田';
        //BusinessUnit__c bu = createBusinessUnit(true);
        acc.Smx_BelongsOfficeDomesticBusiness__c = bu.Id;
        System.debug('acc1 '+JSON.serialize(acc));
        insert acc;
        
        //Create Contact
        Contact con = new Contact();
        con.lastName = 'Test LastName';
        con.AccountId = acc.Id;
        
        Insert con;
        
        //Create Location
        SVMXC__Site__c loc = new SVMXC__Site__c();
        loc.Name = 'Location A';
        loc.SMAX_PS_Active__c = True;
        loc.SVMXC__Account__c = acc.Id;
        loc.SVMXC__Location_Type__c = 'Internal';
        loc.SMAX_PS_Location_Code__c = 'LC01';
        loc.SMAX_PS_Sales_Office_Flag__c = True;
        loc.SVMXC__Partner_Account__c = acc.Id;
        loc.SVMXC__Partner_Contact__c = con.Id;
        
        Insert loc;
        
        //Create Product Categories
        Smx_ProductCategories__c productCat = new Smx_ProductCategories__c();
        productCat.Name = 'Test category2';
        productCat.Smx_ProductClassification__c = '00001';
        
        Insert productCat;
        
        Smx_BusinessFields__c bf = new TestDataUtil().createBusinessFields(false);
        bf.Name = '事業横断';
        insert bf;
        
        Smx_Maker__c maker = new TestDataUtil().createSmx_Maker(true);
        
        //Create Product
        Product2 prod1 = new TestDataUtil().createProduct2(false);
        prod1.Smx_Maker__c = maker.Id;
        prod1.Smx_FieldCode__c = bf.Id;
        prod1.SVMXC__Product_Line__c = '100000101';
        prod1.Smx_OutputObject__c = '2';
        prod1.Smx_DiscontinuedFlg__c = '2';
        prod1.Smx_IsDelete__c = false;
        insert prod1;
​
        PrefectureMaster__c  pfm = new PrefectureMaster__c ();
        pfm.Name = 'Karnatak';
        pfm.smx_namekanahalf__c = 'KA';
        pfm.PrefectureCode__c = '99';
        Insert pfm;
        
        //Create Case
        Case c = new Case();
        c.AccountId = acc.Id;
        c.ContactId = con.Id;
        c.Status = '保留中';
        c.Origin = 'Web';
        c.SVMXC__Site__c = loc.Id;
        c.SVMXC__Product__c = prod1.Id;
        
        Insert c;
        
        //Create Work Order records
        SVMXC__Service_Order__c wo = new SVMXC__Service_Order__c();
        wo.SVMXC__Company__c = acc.Id;
        wo.SVMXC__Contact__c = con.Id;
        wo.SVMXC__Order_Status__c = 'Open';
        wo.SVMXC__Order_Type__c = 'FCA';
        wo.SVMXC__Site__c = loc.Id;
        wo.SVMXC__Case__c = c.Id;
       // wo.SVMXC__Top_Level__c=ip1.Id;
       // wo.SVMXC__Component__c=ip1.Id;
        wo.SMAX_PS_Sales_Office__c= bu.Id;
        wo.SVMXC__State__c = 'Karnatak';
        Test.startTest();
        Insert wo;
​
        SVMXC__RMA_Shipment_Order__c partsOrder = new SVMXC__RMA_Shipment_Order__c();
        Id partsOrderRecordTypeId = Schema.SObjectType.SVMXC__RMA_Shipment_Order__c.getRecordTypeInfosByDeveloperName().get('Shipment').getRecordTypeId();
        partsOrder.SVMXC__Company__c = acc.Id;
        partsOrder.SVMXC__Order_Type__c = '  SVMX In-House Transfer';
        partsOrder.SVMXC__Order_Status__c = 'Draft';
        partsOrder.SVMXC__Source_Location__c = loc.Id;
        partsOrder.SMAX_PS_From_Location_Level1__c = loc.Id;
        //partsOrder.SMAX_PS_From_Location_Level2__c = loc.Id;
        //partsOrder.SMAX_PS_From_Location_Level3__c = loc.Id;
        partsOrder.SVMXC__Destination_Location__c = loc.Id;
        partsOrder.SMAX_PS_To_Location_Level1__c = loc.Id;
        //partsOrder.SMAX_PS_To_Location_Level2__c = loc.Id;
        //partsOrder.SMAX_PS_To_Location_Level3__c = loc.Id;
        partsOrder.RecordTypeId  =partsOrderRecordTypeId;
        partsOrder.SVMXC__Contact__c = con.Id;
        partsOrder.SVMXC__Expected_Delivery_Date__c = date.newInstance(2020, 12, 30);
        partsOrder.SVMXC__Case__c = c.Id;
        partsOrder.SVMXC__Service_Order__c = wo.Id;
        
        //Insert partsOrder;
        
        List<SVMXC__RMA_Shipment_Order__c> partsOrderList = new List<SVMXC__RMA_Shipment_Order__c>();
        map<String,List<SVMXC__RMA_Shipment_Order__c>> detailSobjectMap = new map<String, List<SVMXC__RMA_Shipment_Order__c>>();
        
        partsOrderList.add(partsOrder);
        Insert partsOrderList;
        
        detailSobjectMap.put('Parts Order',partsOrderList);
        
        List<SVMXC__RMA_Shipment_Line__c> partsOrderLineList = new List<SVMXC__RMA_Shipment_Line__c>();
        map<String,List<SVMXC__RMA_Shipment_Line__c>> detailSobjectMap1 = new map<String, List<SVMXC__RMA_Shipment_Line__c>>();
        
        //Parts Order Line  
        SVMXC__RMA_Shipment_Line__c partsorderLine = new SVMXC__RMA_Shipment_Line__c();
        partsorderLine.SVMXC__RMA_Shipment_Order__c = partsOrder.Id;
        partsorderLine.SVMXC__Product__c = prod1.Id; 
        partsorderLine.SMAX_PS_Product_Batch_Lot_Enabled__c = True;
        partsorderLine.SVMXC__Actual_Quantity2__c = 2;
        partsorderLine.SVMXC__Expected_Quantity2__c = 2;
        
        Insert partsorderLine;
        //partsOrderLineList.add(partsorderLine);
        //Insert partsOrderLineList;
        //detailSobjectMap1.put('Parts Order Lines',partsOrderLineList);
​
            
        SVMXC.SFM_WrapperDef.SFM_TargetRecord request = new SVMXC.SFM_WrapperDef.SFM_TargetRecord();
        
        SVMXC.SFM_WrapperDef.SFM_StringMap tempStringMap = new SVMXC.SFM_WrapperDef.SFM_StringMap('SVMX_recordId',partsOrderList[0].Id);
        request.stringMap.add(tempStringMap);
        
        request.headerRecord.objName = 'SVMXC__RMA_Shipment_Order__c';
        
        SVMXC.SFM_WrapperDef.SFM_Record woRecord = new SVMXC.SFM_WrapperDef.SFM_Record();
        SVMXC.SFM_WrapperDef.SFM_StringMap contractStringMap = new SVMXC.SFM_WrapperDef.SFM_StringMap('ID',partsOrderList[0].Id);
        woRecord.targetRecordAsKeyValue.add(contractStringMap);
        woRecord.targetRecordId = partsOrderList[0].Id;
        request.headerRecord.records.add(woRecord);
        
        List<SVMXC.SFM_WrapperDef.SFM_TargetRecordObject> detailRecordsList = new List<SVMXC.SFM_WrapperDef.SFM_TargetRecordObject>();
        SVMXC.SFM_WrapperDef.SFM_Record contractItemRecord = new SVMXC.SFM_WrapperDef.SFM_Record();
        SVMXC.SFM_WrapperDef.SFM_StringMap contractItemMap = new SVMXC.SFM_WrapperDef.SFM_StringMap('ID',partsorderLine.Id);
        contractItemRecord.targetRecordAsKeyValue.add(contractItemMap);
        contractItemRecord.targetRecordId = partsorderLine.Id;
        
        SVMXC.SFM_WrapperDef.SFM_TargetRecordObject contractItemObject = new SVMXC.SFM_WrapperDef.SFM_TargetRecordObject();
        contractItemObject.aliasName = '';
        contractItemObject.pageLayoutId = 'SVMXC__RMA_Shipment_Line__c';
        contractItemObject.objName = 'SVMXC__RMA_Shipment_Line__c';
        contractItemObject.parentColumnName = 'SVMXC__RMA_Shipment_Order__c';
        contractItemObject.records.add(contractItemRecord);
        detailRecordsList.add(contractItemObject);
        request.detailRecords = detailRecordsList ;
       
        
        
        SVMXC.SFM_WrapperDef.SFM_PageData response = SMAX_PS_GetStorageBin.populateStorageBin(request);       
        Test.stopTest();
        
    }
     public static BusinessUnit__c createBusinessUnit(Boolean insFlg) {
        BusinessUnit__c insBu1 = new BusinessUnit__c();
        insBu1.Name = 'test01';
        insBu1.Smx_Chief__c= userInfo.getUserId();
        insBu1.Smx_Type__c = '営業所（人事）';
        insBu1.Smx_Project__c = '10.国内営業';
        
        BusinessUnit__c insBu2 = new BusinessUnit__c();
        insBu2.Name = 'test01';
        insBu1.Smx_Chief__c= userInfo.getUserId();
        insBu2.Smx_Type__c = '営業所（人事）';
        insBu2.Smx_Project__c = '10.国内営業';
        insert insBu2;
        
        insBu1.Smx_ParentBusinessUnitid__c = insBu2.Id;
        if(insFlg) {
            insert insBu1;
        }
        return insBu1;
    }
	}