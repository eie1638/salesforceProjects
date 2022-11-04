public class BatchApexIPL implements Database.Batchable<sObject> , Database.stateful {
     public Database.QueryLocator start(Database.BatchableContext BC){
        return Database.getQueryLocator([Select Id, Parts_Order__c,Processed__c,Error_message__c,In_Error__c
                                         From Inventory_Process_Log__c Where Processed__c=False]);
    }
      public void execute(Database.BatchableContext BC, List<Inventory_Process_Log__c>newList){
           Map<Id, list<SVMXC__Product_Stock__c>> productsMap= new Map<ID, list<SVMXC__Product_Stock__c>>();
           list<Inventory_Process_Log__c> inventorylist= new List<Inventory_Process_Log__c>();
           list<SVMXC__Product_Stock__c> productStockList = new list<SVMXC__Product_Stock__c>();
           map<Id,list<SVMXC__RMA_Shipment_Line__c>>AllPLmap = new map<Id,list<SVMXC__RMA_Shipment_Line__c>>();
           Set<ID> setloid= new Set<ID>();
           set<ID>setID= new set<ID>();
           for(Inventory_Process_Log__c iplc :newList ){
              if(iplc.Parts_Order__c!=null){
                 setID.add(iplc.Parts_Order__c);
              }
          }
          if(!setID.isempty()){
          list<SVMXC__RMA_Shipment_Line__c> productLine = [Select Id,Name,Part__c,From_Location__c,Qty__c,
                                                           SVMXC__RMA_Shipment_Order__c From SVMXC__RMA_Shipment_Line__c
                                                           Where SVMXC__RMA_Shipment_Order__c IN:setID ];
              if(productLine!=null){
              for(SVMXC__RMA_Shipment_Line__c pline : productLine){
                  if(AllPLmap.containskey(pline.SVMXC__RMA_Shipment_Order__c)){
                        AllPLmap.get(pline.SVMXC__RMA_Shipment_Order__c).add(pline);
                  }
                  else{
                       AllPLmap.put(pline.SVMXC__RMA_Shipment_Order__c, new list<SVMXC__RMA_Shipment_Line__c>{pline});
                  }
                  setloid.add(pline.From_Location__c);
              }
              }
              if(!setloid.isEmpty()){
                List<SVMXC__Product_Stock__c> productshlist= [Select ID,SVMXC__Location__c,SVMXC__Quantity2__c,SVMXC__Product__c ,SVMXC__Status__c
                                                              From SVMXC__Product_Stock__c Where SVMXC__Location__c IN: setloid
                                                              AND SVMXC__Status__c='Available' ];
                if(!productshlist.isEmpty()){
                   for(SVMXC__Product_Stock__c ps : productshlist){
                    if(productsMap.containsKey(ps.SVMXC__Location__c)){
                        productsMap.get(ps.SVMXC__Location__c).add(ps);
                    }
                    else{
                        productsMap.put(ps.SVMXC__Location__c,new list<SVMXC__Product_Stock__c>{ps});
                    }
                 }
                }
            }
          for(Inventory_Process_Log__c iplc : newList){
              if(AllPLmap.containsKey(iplc.Parts_Order__c) && !AllPLmap.isEmpty()){
                  if(AllPLmap.get(iplc.Parts_Order__c)!=null && !AllPLmap.get(iplc.Parts_Order__c).isEmpty()) {
                  for(SVMXC__RMA_Shipment_Line__c pl : AllPLmap.get(iplc.Parts_Order__c)){
                      if(pl.from_location__c!=null && pl.Part__c!=null && pl.Qty__c!=null ){
                           Integer PSvalue=0;
                          if(productsMap!=null && productsMap.containsKey(pl.from_Location__c) && productsMap.get(pl.from_Location__c)!=null){
                              for(SVMXC__Product_Stock__c ps: productsMap.get(pl.From_Location__c)){
                                   if(ps.SVMXC__Product__c==pl.Part__c){
                                    ps.SVMXC__Quantity2__c=ps.SVMXC__Quantity2__c+pl.Qty__c;
                                       PSvalue++;
                                   if(productStockList.contains(ps)){
                                Integer p= productStockList.indexOf(ps);//to remove extra record in ps
                                productStockList.remove(p);
                            }
                                  productStockList.add(ps);
                                   }
                              }
                          }
                          //if there is no record present in product stock then we need to insert
                          if(PSvalue==0){
                        SVMXC__Product_Stock__c ps= new SVMXC__Product_Stock__c();
                        ps.SVMXC__Location__c=pl.From_Location__c;
                        ps.SVMXC__Quantity2__c=pl.Qty__c;
                        ps.SVMXC__Status__c='Available';
                        ps.SVMXC__Product__c=pl.Part__c;
                        productStockList.add(ps);
                        if(productsMap.containsKey(ps.SVMXC__Location__c)){
                        productsMap.get(ps.SVMXC__Location__c).add(ps);
                    }
                    else{
                        productsMap.put(ps.SVMXC__Location__c,new list<SVMXC__Product_Stock__c>{ps});
                         }
                      }
                      }
                      if(pl.Qty__c==null){
                        iplc.In_Error__c=true;
                           if(iplc.Error_message__c!=null){
                           iplc.Error_message__c=iplc.Error_message__c+' ,'+pl.Name+'no line qty found';
                           }
                          else{
                               iplc.Error_message__c=pl.Name+'no line qty found';
                           }
                      }
                       if(pl.From_Location__c==null){
                        iplc.In_Error__c=true;
                        if(iplc.Error_message__c!=null){
                            iplc.Error_message__c=iplc.Error_message__c+' ,'+pl.Name+' no location info found ';
                        }
                        else{
                            iplc.Error_message__c=pl.Name+' no location info found ';
                        }
                      }
                       if(pl.Part__c==null){
                        iplc.In_Error__c=true;
                        if(iplc.Error_message__c!=null){
                            iplc.Error_message__c=iplc.Error_message__c+' ,'+pl.Name+' no part info found ';
                        }
                        else{
                            iplc.Error_message__c=pl.Name+' no part info found ';
                        }
                      }
                    }
                  }
              }
              else{
                iplc.Processed__c=true;
                iplc.In_Error__c=true;
                if(iplc.Error_message__c!=null){
                iplc.Error_message__c=iplc.Error_message__c+'no line found';
                }
                else{
                    iplc.Error_message__c='no line found';
                }
            }
            iplc.Processed__c=true;
            inventorylist.add(iplc);
              }
              if(!productStockList.isEmpty()){
            upsert productStockList;
        }
              if(!inventorylist.isEmpty()){
            update inventorylist;
        }
          }
          }
    public void finish(Database.BatchableContext BC){
    }
}