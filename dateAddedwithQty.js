$sfm_records.get(sfmData => {
	function addHours(numOfHours, date = new Date()) {
		date.setTime(date.getTime() + numOfHours * 60 * 60 * 1000);
		return date;
	}
	debugger;
	if (sfmData) {
		sfmData = JSON.parse(sfmData);
		var details = sfmData.details;
		if (details) {
			var request = {};
			var LTSection = [];
			var LTSectionId = [];
			var detailKeys = Object.keys(details); 
			for (var i = 0; i < detailKeys.length; i++) {
				var eachDetail = details[detailKeys[i]];
				if (eachDetail.name === 'Labor' || eachDetail.name === 'Travel') {
					LTSection.push(eachDetail);
					LTSectionId.push(detailKeys[i]);

				}
			}
			if (LTSection.length > 1) {
				for (var i = 0; i < LTSection.length; i++) {
					if (LTSection[i].lines && LTSection[i].lines.length > 0) {
						var lines = LTSection[i].lines;
						var records = [];
						for (var j = 0; j < lines.length; j++) {
							var record = lines[j];
							var startDate = record['SVMXC__Start_Date_and_Time__c'].fieldvalue.key
							var Qty = record['SVMXC__Actual_Quantity2__c']
							var date = new Date(startDate)
							var endDate = addHours(Qty, date)
							if (Qty) {
								request.details = request.details || {};
								request.details[LTSectionId[i]] = request.details[LTSectionId[i]] || [];
								request.details[LTSectionId[i]].push({
									'index': '' + j,
									'fields': [{
										'name': 'SVMXC__End_Date_and_Time__c',
										'value': endDate
									}]
								});
								if (request) {
									$sfm_records.setFieldValue(request, result => {
										$response({
											status: 'success',
											error: '',
											error_message: ''
										});

									});
								} else {
									$response({
										status: 'error',
										error: '',
										error_message: 'update faild'
									});
								}
							}
						}
					} else {
						$response({
							status: 'error',
							error_message: 'No  Lines found'
						});
					}
				}

			} else {
				$response({
					status: 'error',
					error_message: 'No  section found'
				});
			}
		} else {
			$response({
				status: 'error',
				error_message: 'No detail lines found'
			});
		}
	} else {
		$response({
			'status': 'error',
			'error_message': 'Invalid response from $sfm_records.get API'
		});
	}
});