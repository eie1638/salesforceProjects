$sfm_records.get(sfmData => {
debugger;
  if (sfmData) {
    sfmData = JSON.parse(sfmData);
    var request = {};
    var details = sfmData.details;
    if (details) {
      var laborSection = null,
        laborSectionId = null,
        detailKeys = Object.keys(details);
      for (var idx = 0; idx < detailKeys.length; idx++) {
        var eachDetail = details[detailKeys[idx]];
        if (eachDetail.name=== 'Labor' || eachDetail.name=== 'labor') {
          laborSection = eachDetail;
          laborSectionId = detailKeys[idx];
          break;
        }
      }
      if (laborSection) {
        if (laborSection.lines && laborSection.lines.length > 1) {
          var previousLaborLine = laborSection.lines[laborSection.lines.length - 2];
          if (previousLaborLine['SVMXC__End_Date_and_Time__c'] &&
            previousLaborLine['SVMXC__End_Date_and_Time__c'].fieldvalue &&
            previousLaborLine['SVMXC__End_Date_and_Time__c'].fieldvalue.value &&
            previousLaborLine['SVMXC__End_Date_and_Time__c'].fieldvalue.value.length > 0) {
            var lastLineEndTime = previousLaborLine['SVMXC__End_Date_and_Time__c'].fieldvalue.value;
            request.details = request.details || {};
            request.details[laborSectionId] = request.details[laborSectionId] || [];
            request.details[laborSectionId].push({
              'index': '' + laborSection.lines.length - 1,
              'fields': [{
                'name': 'SVMXC__Start_Date_and_Time__c',
                'value': lastLineEndTime
              }]
            });
            $sfm_records.setFieldValue(request, result => {
              console.log(JSON.parse(result));
              $response({
                status: 'success'
              });
            });
          } else {
            $response({
              status: 'fail',
              error_message: 'Last Labor line End date time is empty '
            });
          }
        } else {
          //No Labor lines. We can add record with out snippet changes
          $response({
            status: 'success',
            error_message: ''
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