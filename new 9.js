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
		/*console.log('in snippet : db response = ', response);
		var parentip = null;
		var topip = null;
		if (!response.error) {
			if (response.results && (response.results.length)) {
				if (response.IP_Type__c === 'system') {
					parentip = response.SVMXC__Parent__c;
					var request = {
						"details": {
							"fields": [{
								"name": "Installed_Product__c",
								"value": "parentip"
							}]
						}
					};
					$sfm_records.setFieldValue(request, (res) => {
						var currentRecord = JSON.parse(res);
						var result = {
							status: 'success',
							error: '',
							error_message: '',
						}
						$response(result);
					});

				} else {
					topip = response.SVMXC__Top_Level__c;
					var request = {
						"details": {
							"fields": [{
								"name": "Installed_Product__c",
								"value": "topip"
							}]
						}
					};
					$sfm_records.setFieldValue(request, (res) => {
						var currentRecord = JSON.parse(res);
						var result = {
							status: 'success',
							error: '',
							error_message: '',
						}
						$response(result);
					});

				}
			} else {
				$response({
					status: 'success',
					error: '',
					error_message: 'No result found'
				});
			}


		} else {
			$response({
				status: 'error',
				error: res.error,
				error_message: 'Invalid response form db api',
			});
		}*/
	}
	)

});