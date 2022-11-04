$env.get(function(environment) {
	debugger;
	if (environment === 'FSA_IPAD' || environment === 'FSA_ANDROID' || environment === 'FSA_MFL' || environment === 'ServiceMaxGO') {
		$event.get(sfmdata => {
			console.log('in snippet: event response = ', record.currentRec);
			var parsedRecord = JSON.parse(record);
			var currRecord = parsedRecord.currentRec['Installed_Product__c'].fieldvalue.key;
			var filterCondition1 = {
				sequence: 1,
				left_operand: 'Id',
				operator: '=',
				right_operand: [currRecord],
				right_operand_type: 'Value'
			};
			var queryParams = {
				object: 'SVMXC__Installed_Product__c',
				fields: ['IP_Type__c', 'Id'],
				filter: [filterCondition1]
				/*Order: [{
					queryField: '',
					sortingOrder: ''
				}],
				AdvancedExpression: '( 1)'*/
			};
			$db.get(queryParams, function(queryParams, response) {
				console.log('in snippet : db response = ', response);
				var parentip = null;
				var topip = null;
				if (!response.error) {
					if (response.results && response.results.length) {
						if (queryParams.IP_Type__c === 'System') {
							parentip = response.SVMXC__Parent__c;

						} else {
							topip = response.SVMXC__Top_Level__c;
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
				}
			})


		})
	} else {
		$response({
			status: 'error',
			error: res.error,
			error_message: 'invalid environment',
		});
	}
});