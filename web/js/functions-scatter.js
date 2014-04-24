$(document).ready(function() {

  var colorArray = ['#AA4643', '#4572A7', '#89A54E', '#80699B', '#3D96AE', '#DB843D', '#92A8CD', '#A47D7C', '#B5CA92'];
  var color3 = ['#cc0000','#cc6600','#009900'];

  var options2 = {
    chart: {
      renderTo: 'container1',
      type: 'scatter',
      zoomType: 'xy',
      plotBackgroundColor: '#fffdf6'
    },
    credits: {
        enabled: false
    },
    colors: colorArray,
    title: {
      text: 'Dashboard 2 - BMI vs Age'
    },
    xAxis: {
        title: {
            text: 'Age'
        }
    },
    yAxis: {
      title: {
        text: 'BMI'
      }
    },
    series: [{
        name: 'Female',
        color: '#ff99ff',
        data: []
    },{
        name: 'Male',
        color: '#3399ff',
        data: []
    }],
    plotOptions: {
    },
    tooltip: {
        headerFormat: '<b>{series.name}</b><br>',
        pointFormat: 'Age: {point.x} - BMI: {point.y}'
    }
  };  
      
  $.getJSON('../data/visits_with_occupation_50000.json', function(data) {

    $.each(data, function(key, val) {
            
      // option2 calcs
      if (val.gender == 'F') {
        options2.series[0].data.push([val.calculated_age,val.calculated_bmi]);
      } else {
        options2.series[1].data.push([val.calculated_age,val.calculated_bmi]);
      }
       
      
      
    });

    var chart2 = new Highcharts.Chart(options2);
    
  });
  
}); // $(document).ready(function() {