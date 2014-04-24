<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8">
    <title>Healthy Measures - Test Visualizations</title>

    <link rel="stylesheet" href="../css/bootstrap.css">
    <link rel="stylesheet" href="../css/bootstrap-responsive.css" media="screen">
    <link rel="stylesheet" href="../css/styles.css">


    <script type="text/javascript" src="../js/jquery-1.10.2.js"></script>
    <script src="//code.highcharts.com/highcharts.js"></script>
    <script src="//code.highcharts.com/highcharts-more.js"></script>
    <script type="text/javascript" src="../js/regression.js"></script>
    <script type="text/javascript" src="../js/functions-server.js"></script>
   
    
</head>
<body>

<div class="container logo-container">
    <div class="row">
        <div class="span12" style="text-align: center">
            <img src="../img/cdc_logo.png" style="float: left" />
            <div class="title">DRAFT</div>
            <img src="../img/cirg_uw_logo.png" style="float: right" />
        </div>
    </div>
</div>

<div class="container">
    
    <div class="row">
        <div class="span12">
            
            <h1>Healthy Measures - Test Visualizations - Server Data</h1>

            
            <?php
            $mysqli_connection = new MySQLi('localhost', 'mark47', '', 'ihe2014', 3306);
            if($mysqli_connection->connect_error){
               echo "<p>Not connected to server, error: ".$mysqli_connection->connect_error."</p>";
            }
            $return_arr = array();
            $result = mysqli_query($mysqli_connection,"SELECT * FROM healthy_weight_obs");
//            while($row = mysqli_fetch_array($result))
//              {
//              echo $row['calculated_age'] . " - " . $row['calculated_bmi'];
//              echo "<br>";
//              }
              
            while($row = mysqli_fetch_array($result)) {
                $row_array['calculated_bmi'] = floatval($row['calculated_bmi']);
                $row_array['calculated_age'] = floatval($row['calculated_age']);
                $row_array['gender'] = $row['gender'];
                array_push($return_arr,$row_array);
            }
            $setArray = json_encode($return_arr);
//            $foo = mysqli_query($mysqli_connection,
//                "SELECT avg(t1.calculated_bmi) as median_val FROM (
//                SELECT @rownum:=@rownum+1 as `row_number`, d.calculated_bmi
//                  FROM health_weight_obs d,  (SELECT @rownum:=0) r
//                  WHERE 1
//                  ORDER BY d.calculated_bmi
//                ) as t1, 
//                (
//                  SELECT count(*) as total_rows
//                  FROM health_weight_obs d
//                  WHERE 1
//                ) as t2
//                WHERE 1
//                AND t1.row_number in ( floor((total_rows+1)/2), floor((total_rows+2)/2) )"
//            );
//            while($row2 = mysqli_fetch_array($foo)) {
//                echo $row2;
//            }
//            //print_r($foo);
//            echo "<p>".$foo."</p>";
            mysqli_close($mysqli_connection);
            ?>            
            <p>These use data from the IHE2014 VM database. This data set has been converted to a JSON array. Currently only 25 rows.</p>

            <p>Note: The box plot charts use the following data points (I'm not sure if this is the standard for box plots):</p>
            <ul>
                <li>Min value</li>
                <li>1st quartile</li>
                <li>Median</li>
                <li>3rd quartile</li>
                <li>Max value</li>
            </ul>

            <br /><br />

            <div id="container1">
            </div>


            <br /><br />

            <div id="container2">
            </div>

            <br /><br />

            <div id="container3">
            </div>

            <br /><br />

            <p><a href="index-misc.html">View older tests using dummy data set</a></p>



        </div>
    </div>
    

</div>
    <script>
    $(document).ready(function(){
        function median(values) {
   
      var minSet;
      var lowQ;
      var medianSet;
      var highQ;
      var maxSet;

      values.sort( function(a,b) {return a - b;} );
      //console.log(values);
      var half = Math.floor(values.length/2);

      var low = Math.floor(values.length/4);
      var high = Math.floor(3 * (values.length/4));

      var low5 = Math.floor(values.length/20);
      var high5 = Math.floor(19 * (values.length/20));
   
      //if(values.length % 2) {
          medianSet = values[half];
          lowQ = (values[low] + values[low]);
          highQ = (values[high-1] + values[high]) / 2.0;
          minSet = values[low5];
          maxSet = values[high5];
      //} else {
      //    medianSet = (values[half-1] + values[half]) / 2.0;
      //    lowQ = values[low];
      //    highQ = values[high];
      //    minSet = (values[low5-1] + values[low5]) / 2.0;
      //    maxSet = (values[high5-1] + values[high5]) / 2.0;
      //}



      //minSet = values[0];
      //maxSet = values[values.length-1];

      var returnSet = [minSet,lowQ,medianSet,highQ,maxSet];
      return returnSet;
    }
        
        
        //var myArray2 = <?php echo $setArray; ?>;
        var testArray = [];
        function roastIt(boast) {
            $.each(boast, function(i, item) {
                testArray.push(item.calculated_bmi);
            });
        };
        var toast = $.parseJSON('<?php echo $setArray; ?>');
        roastIt(toast);
        var goat = median(testArray);
        console.log(goat);
    });
    </script>            
</body>
</html>

