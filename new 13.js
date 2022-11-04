$env.get(function(environment) {
	debugger;
	if (environment === 'FSA_IPAD' || environment === 'FSA_ANDROID' || environment === 'FSA_MFL' || environment === 'ServiceMaxGO') {
	 $event.get(function(record) {
	var request = {};
	request.details = {};
	console.log(record);
	var parsedRecord = JSON.parse(record);
	request.details[parsedRecord.currentSectionId] = [];
	var wdid = parsedRecord.currentRec['Id'];
	console.log(record);
	//var parsedRecord = JSON.parse(record);

	var currIPkey = parsedRecord.currentRec['Installed_Product__c'].fieldvalue.value;
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
			
			if (response.results) {
				var prntId = response.results[0].SVMXC__Parent__c;
				var pIPName = response.results[0].SVMXC__Parent__r;
			    var TopId = response.results[0].SVMXC__Top_Level__c;
				var TIPname =response.results[0].SVMXC__Top_Level__r;
			    var iptype = response.results[0].IP_Type__c;
				if (iptype =='System') {
					request.details[parsedRecord.currentSectionId].push({
						'index': parsedRecord.currentRecIndex,
						'fields': [{
							'name': 'Parent_IP__c',
							'value': prntId , 
							'value1':pIPName
						}]
					})
				}else{
					request.details[parsedRecord.currentSectionId].push({
						'index': parsedRecord.currentRecIndex,
						'fields': [{
							'name': 'Top_level_IP__c',
							'value': TopId,
							'value1':TIPname
						}]
					})
				}
				console.log(request);
				
			} else {
				
				$response({
					status: 'error',
					error: '',
					error_message: 'db api is  invalid '
				});
				
			}
			if(request){
				$sfm_records.setFieldValue(request, result => {
						$response({
				        status: 'success',
				        error: '',
				        error_message: ''
				      });
						
					});
			}else{
				$response({
					status: 'error',
					error: '',
					error_message: 'update faild'
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
	}
	else {
		$response({
			status: 'error',
			error: res.error,
			error_message: 'invalid environment',
		});
	}
});