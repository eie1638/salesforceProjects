$sfm_records.get(sfmData => {
	debugger;
	function addHours(numOfHours, date = new Date()) {
		date.setTime(date.getTime() + numOfHours * 60 * 60 * 1000);
		return date;
	}
	if (sfmData) {
		sfmData = JSON.parse(sfmData);
		var details = sfmData.details;
		if (details) {
			var detailKeys = Object.keys(details);
			for (var i = 0; i < detailKeys.length; i++) {
				var request = {};
				var LTSection = null;
				var LTSectionId = null;
				var eachDetail = details[detailKeys[i]];
				if (eachDetail.name === 'Labor' || eachDetail.name === 'Travel') {
					LTSection = eachDetail;
					LTSectionId = detailKeys[i];
					if (LTSection) {
						if (LTSection.lines && LTSection.lines.length > 0) {
							var lines = LTSection.lines;
							var records = [];
							for (var j = 0; j < lines.length; j++) {
								var record = lines[j];
								var startDate = null;
								startDate = record['SVMXC__Start_Date_and_Time__c'].key;
								if (!startDate) {
									startDate = record['SVMXC__Start_Date_and_Time__c'].fieldvalue.key;
								}
								var partsQty = record['SVMXC__Actual_Quantity2__c']
								var date = new Date(startDate)
								var endDateTime = addHours(partsQty, date)
								if (partsQty) {
									request.details = request.details || {};
									request.details[LTSectionId] = request.details[LTSectionId] || [];
									request.details[LTSectionId].push({
										'index': '' + j,
										'fields': [{
											'name': 'SVMXC__End_Date_and_Time__c',
											'value': endDateTime
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
								error_message: 'No Labor Lines found'
							});
						}
					} else {
						$response({
							status: 'error',
							error_message: 'No Labor section found'
						});
					}
				} else {
					$response({
						status: 'error',
						error_message: 'NO DATA FOUND'
					});
				}

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