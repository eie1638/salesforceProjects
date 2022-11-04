$event.get(function(record) {
	debugger;
	var request = {};
	request.details = {};
	console.log(record);
	var parsedRecord = JSON.parse(record);
	request.details[parsedRecord.currentSectionId] = [];
	var wdid = parsedRecord.currentRec['Id'];
	console.log(record);
	var parsedRecord = JSON.parse(record);

	var currIPkey = parsedRecord.currentRec['Installed_Product__c'].fieldvalue.key;
	if (currIPkey) {

		var filterCondition1 = {
			sequence: 1,
			left_operand: 'Id',
			operator: '=',
			right_operand: [currIPkey],
			right_operand_type: 'Value'
		};
		var query_object = {
			object: 'SVMXC__Installed_Product__c',
			fields: ['Name', 'Id', 'SVMXC__Parent__c', 'SVMXC__Top_Level__c', 'Type__c', 'IP_Type__c'],
			filter: [filterCondition1]
		};

		console.log(query_object);
		$db.get(query_object, function(query_object, response) {
			console.log(response);
			console.log(response.results);
			console.log(response.results.length);
            var PID = null;
			var TIP = null;
			if (response.results && response.results.length) {
				if(response.IP_Type__c==='System'){
					PID=response.SVMXC__Parent__c;
					
				}else{
					TIP=response.SVMXC__Top_Level__c;
				}
				
				$response({
					status: 'success',
					error: '',
					error_message: 'dp is  successfully'
				});
			} else {
				$response({
					status: 'error',
					error: '',
					error_message: 'db not found '
				});
			}

		})





	} else {
		$response({
			status: 'error',
			error: '',
			error_message: 'event api is successfully invalied '
		});
	}


});