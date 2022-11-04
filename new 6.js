$env.get(function(environment) {
	debugger;
	if (environment === 'FSA_IPAD' || environment === 'FSA_ANDROID' || environment === 'FSA_MFL' || environment === 'ServiceMaxGO') {
		$event.get(function(record) {
			console.log('in snippet: event response = ', record.currentRec);
			var parsedRecord = JSON.parse(record);
			var currRecStartDate = parsedRecord.currentRec['SVMXC__Start_Date_and_Time__c'].fieldvalue.value;
			var currRecEndDate = parsedRecord.currentRec['SVMXC__End_Date_and_Time__c'].fieldvalue.value;
			var currentRecordId = parsedRecord.currentRec.Id;
			$sfm_records.get(unsavedSfmRecordString => {
				var unsavedSfmRecord = JSON.parse(unsavedSfmRecordString);
				console.log('in snippet: sfm_record response = ', unsavedSfmRecord);
				var details = unsavedSfmRecord.details;
				console.log('details =', details);
				var conflictLocalLaborLines = [];
				var sectionKeys = Object.keys(unsavedSfmRecord.details);
				for (var secIdx = 0; secIdx < sectionKeys.length; secIdx++) {
					var eachSection = unsavedSfmRecord.details[sectionKeys[secIdx]];
					if (eachSection.name === 'Labor') {
						var laborLines = eachSection.lines || [];
						for (var lineIdx = 0; lineIdx < laborLines.length; lineIdx++) {
							var eachLaborLine = laborLines[lineIdx];
							if (eachLaborLine['SVMXC__End_Date_and_Time__c'].fieldvalue.value ==
								currRecEndDate && eachLaborLine['SVMXC__Start_Date_and_Time__c'].fieldvalue.value == currRecStartDate) {
								conflictLocalLaborLines.push(eachLaborLine);
							}

						}
						break;
					}
				}
				if (conflictLocalLaborLines.length != 0) {
					$response({

						'status': 'error',
						'error_message': 'duplicate data found'
					});
				} else {
					$response({
						status: 'success'
					});
				}
				/*var filterCondition1 = {
				  sequence: 1,
				  left_operand: 'SVMXC__End_Date_and_Time__c',
				  operator: '>=',
				  right_operand: [currRecStartDate],
				  right_operand_type: 'Value'
				};
				var filterCondition2 = {
				  sequence: 2,
				  left_operand: 'SVMXC__Start_Date_and_Time__c',
				  operator: '<=',
				  right_operand: [currRecEndDate],
				  right_operand_type: 'Value'
				};
				var filterCondition3 = {
				  sequence: 3,
				  left_operand: 'Id',
				  operator: '!=',
				  right_operand: [currentRecordId],
				  right_operand_type: 'Value'
				};
				var filterCondition4 = {
				  sequence: 4,
				  left_operand: 'SVMXC__Line_Type__c',
				  operator: '=',
				  right_operand: ['Labor'],
				  right_operand_type: 'Value'
				};
				var queryParams = {
				  object: 'SVMXC__Service_Order_Line__c',
				  fields: ['SVMXC__Actual_Quantity2__c', 'SVMXC__Billable_Quantity__c',
				    'SVMXC__Start_Date_and_Time__c', 'Name'
				  ],
				  filter: [filterCondition1, filterCondition2, filterCondition3, filterCondition4],
				  Order: [{
				    queryField: 'SVMXC__Actual_Quantity2__c',
				    sortingOrder: 'ASC'
				  }],
				  AdvancedExpression: currentRecordId ? '(1 AND 2 AND 3 AND 4)' : '(1 AND 2 AND 4)'
				};
				$db.get(queryParams, function(queryParams, response) {
				  console.log('in snippet : db response = ', response);
				  if (!response.error) {
				    if (response.results && (response.results.length || conflictLocalLaborLines.length)) {
				      var names = [];
				      for (var i = 0; i < response.results.length; i++) {
				        names.push(response.results[i].Name);
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
				})*/
			})
		});
	} else {
		$response({
			status: 'error',
			error: res.error,
			error_message: 'invalid environment',
		});
	}
});