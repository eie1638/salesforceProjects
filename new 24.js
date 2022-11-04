$sfm_records.get(sfmData => {
	debugger;
	if (sfmData) {
		sfmData = JSON.parse(sfmData);
		var details = sfmData.details;
		if (details) {
			
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