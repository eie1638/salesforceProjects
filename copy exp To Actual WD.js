$sfm_records.get(sfmData => {
	debugger;
	if (sfmData) {
		sfmData = JSON.parse(sfmData);
		var header = sfmData.header;
		var details = sfmData.details;
		if (details) {
			var details = sfmData.details;
			var request = {};
			var partSection = null;
			var partSectionId = null;
			var detailKeys = Object.keys(details);
			for (var i = 0; i < detailKeys.length; i++) {
				var eachDetail = details[detailKeys[i]];
				if (eachDetail.name === 'parts') {
					partSection = eachDetail;
					partSectionId = detailKeys[i];
					break;
				}
			}
			if (partSection) {
				if (partSection.lines && partSection.lines.length > 0) {
					var lines = partSection.lines;
					var records = [];
					for (var j = 0; j < lines.length; j++) {
						var record = lines[j];
						var ExpQty = record['expected_qty__c']
						if (ExpQty) {
							request.details = request.details || {};
							request.details[partSectionId] = request.details[partSectionId] || [];
							request.details[partSectionId].push({
								'index': '' + j,
								'fields': [{
									'name': 'SVMXC__Actual_Quantity2__c',
									'value': ExpQty
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
						}else {
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