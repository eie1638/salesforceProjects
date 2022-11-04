$sfm_records.get(sfmData => {
	debugger;
	if (sfmData) {
		var header = sfmData.header;
		var HeaderLoc = header.SVMXC__Site__c;
		var HeaderLoc1 = HeaderLoc.fieldvalue.key;
		var HeaderLoc2 = HeaderLoc.fieldvalue.value;
		var details = sfmData.details;
		if (details) {
			var request = {};
			var laborSection = null;
			var laborSectionId = null;
			var detailKeys = Object.keys(details);
			for (var i = 0; i < detailKeys.length; i++) {
				var eachDetail = details[detailKeys[i]];  
				if (eachDetail.name === 'Labor') {
					laborSection = eachDetail;
					laborSectionId = detailKeys[i];
					break;
				}
			}

			if (laborSection) {
				if (laborSection.lines && laborSection.lines.length > 0) {
					var lines = laborSection.lines;
					// var records = [];
					for (var j = 0; j < lines.length; j++) {
						var record = lines[j];
						if (record) {
							request.details = request.details || {};
							request.details[laborSectionId] = request.details[laborSectionId] || [];
							request.details[laborSectionId].push({
								'index': '' + j,
								'fields': [{
									'name': 'SVMXC__From_Location__c',
									'value': HeaderLoc1,
									'value1': HeaderLoc2
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
						} else {
							$response({
								status: 'error',
								error_message: 'No expValue found'
							});
						}
					}
				} else {
					$response({
						status: 'error',
						error_message: 'No Labor Lines found'
					});
				}

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
			error_message: 'No detail lines found'
		});
	}
});