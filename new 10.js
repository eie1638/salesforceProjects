$event.get(function(record) {
	debugger;
	console.log(record);
	var parsedRecord = JSON.parse(record);
	var currRecord = parsedRecord.currentRec['Installed_Product__c'].fieldvalue.key;
	var filterCondition1 = {
		sequence: 1,
		left_operand: 'Id',
		operator: '=',
		right_operand: [currRecord],
		right_operand_type: 'Value'
	}
	var queryParams = {
		object: 'SVMXC__Installed_Product__c',
		fields: ['Name', 'SVMXC__Parent__c', 'SVMXC__Top_Level__c', 'Type__c', 'IP_Type__c'],
		filter: [filterCondition1]
	};
	console.log(queryParams);
	$db.get(queryParams, function(queryParams, response) {
		
	})

});