$sfm_records.get(sfmData => {
    debugger;
    if (sfmData) {

        sfmData = JSON.parse(sfmData);
        var header = sfmData.header;

        var request = {};
        request.header = request.header || {};

        var input1 = header.Input1__c;
        var input2 = header.Input2__c;

        var operator = header.Operator__c.fieldvalue.key;
        var result = 0;
        if (!input1) {
            $response({
                status: 'error',
                error: '',
                error_message: 'First Number is Not Present'
            });
        } else if (!input2) {
            $response({
                status: 'error',
                error: '',
                error_message: 'Second Number is Not Present'
            });
        } else if (!operator) {
            $response({
                status: 'error',
                error: '',
                error_message: 'Operands cannot be empty'
            });
        } else {
            if (operator === '+') {
                result = input1 + input2;
            } else if (operator === '-') {
                result = input1 - input2;
            } else if (operator === '*') {
                result = input1 * input2;
            } else if (operator === '/') {
                result = input1 / input2;
            }
            request.header['fields'] = [{
                'name': 'Result_of_Calculation__c',
                'value': result
            }];

            if (request){
                $sfm_records.setFieldValue(request, result => {
                    console.log(JSON.parse(result));
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
                    error_message: 'calculation is not successful'
                });
            }


        }
    }
});