$(document).ready(function() {

  var colorArray = ['#AA4643', '#4572A7', '#89A54E', '#80699B', '#3D96AE', '#DB843D', '#92A8CD', '#A47D7C', '#B5CA92'];
  var color3 = ['#cc0000','#cc6600','#009900'];
  
  var options0 = {
    chart: {
      renderTo: 'container0',
      type: 'column',
      plotBackgroundColor: '#fffdf6'
    },
    legend: {
        reversed: true
    },
    colors: color3,
    title: {
      text: 'Dashboard 1 - BMI category by Age Group'
    },
    xAxis: {
        categories: [
            'Under 20','20-40','40-60','60+'
        ]
    },
    yAxis: {
      title: {
        text: 'Percentage'
      }
    },
    series: [],
    plotOptions: {
        column: {
            stacking: 'percent'
//            dataLabels: {
//                enabled: true,
//                color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white',
//                style: {
//                    textShadow: '0 0 3px black, 0 0 3px black'
//                }
//            }
        }
    },
    tooltip: {
        pointFormat: '<span style="color:{series.color}">{series.name}</span>: {point.percentage:.0f}%<br/>',
        shared: true
    }
  };    

  var options1 = {
    chart: {
      renderTo: 'container1',
      type: 'column',
      plotBackgroundColor: '#fffdf6'
    },
    colors: colorArray,
    title: {
      text: 'King County - BMI Averages by Age and Gender'
    },
    xAxis: {
        categories: [
            'Under 20','20-40','40-60','60+'
        ]
    },
    yAxis: {
      title: {
        text: 'Average BMI'
      },
      tickInterval: 1
    },
    series: [],
    tooltip: {
        formatter: function() {
            return  '<b>'+this.series.name+' Age Range '+ this.x +
                '</b><br>Average BMI: '+ this.y.toFixed(1) +
                '<br >Count: '+this.point.dataCount;
        }
    }
  };

  var options2 = {
    chart: {
      renderTo: 'container2',
      type: 'boxplot',
      plotBackgroundColor: '#fffdf6'
    },
    colors: colorArray,
    title: {
      text: 'King County - BMI by Age and Gender'
    },
    xAxis: {
        categories: [
            'Under 20','20-40','40-60','60+'
        ]
    },
    yAxis: {
      title: {
        text: 'BMI'
      },
      tickInterval: 5
    },
    series: []
  };


var options3 = {
    chart: {
      renderTo: 'container3',
      type: 'boxplot',
      plotBackgroundColor: '#fffdf6'
    },
    colors: colorArray,
    title: {
      text: 'King County - BMI by Ethnicity'
    },
    tooltip: {
	    useHTML: true,
	    headFormat: '{point.key} - {series.name}',
	    pointFormat: '95th quantile: {point.high}<br />Upper quartile: {point.q3}<br />Median: {point.median}<br />Lower quartile: {point.q1}<br />5th quantile: {point.low}'
	 },
    xAxis: {
        categories: [
            'Female','Male'
        ]
    },
    yAxis: {
      title: {
        text: 'BMI'
      },
      startOnTick: false,
      endOnTick: false,
      plotLines: [{
                color: '#E47400',
                value: 25,
                width: 3,
                label: { 
                  text: 'Overweight',
                  align: 'left',
                  style: {
                  	color: '#E47400'	
                  }
                  
                }
            },{
                color: '#f08080',
                value: 30,
                width: 3,
                label: { 
                  text: 'Obese',
                  align: 'left',
                   style: {
                        color: '#f08080'
                    }
                }
            }]
    },
    series: []
  };


  var genderM = 0;
  var genderF = 0;
  var mUnder = 0;
  var mNormal = 0;
  var mOver = 0;
  var fUnder = 0;
  var fNormal = 0;
  var fOver = 0;
  var fChild = 0;
  var fChildTot = 0;
  var fYoung = 0;
  var fYoungTot = 0;
  var fMed = 0;
  var fMedTot = 0;
  var fOld = 0;
  var fOldTot = 0;
  var mChild = 0;
  var mChildTot = 0;
  var mYoung = 0;
  var mYoungTot = 0;
  var mMed = 0;
  var mMedTot = 0;
  var mOld = 0;
  var mOldTot = 0;

  // Option0 variables
  
  // Option2 variables
  var fc = [];
  var fy = [];
  var fm = [];
  var fo = [];
  var mc = [];
  var my = [];
  var mm = [];
  var mo = [];

  // Option3 variables
  var fa = [];
  var fh = [];
  var fw = [];
  var ma = [];
  var mh = [];
  var mw = [];

    // option0 calculations
    var age1n = 0;
    var age1o = 0;
    var age1b = 0;
    var age2n = 0;
    var age2o = 0;
    var age2b = 0;
    var age3n = 0;
    var age3o = 0;
    var age3b = 0;
    var age4n = 0;
    var age4o = 0;
    var age4b = 0;
      
  $.getJSON('../data/visits_with_occupation_50000.json', function(data) {

    $.each(data, function(key, val) {
        
      if (val.calculated_age > 60) {
          if (val.calculated_bmi >= 30 ) age4b++
          else if (val.calculated_bmi >= 25 ) age4o++
          else age4n++
      } else if (val.calculated_age > 40) {
          if (val.calculated_bmi >= 30 ) age3b++
          else if (val.calculated_bmi >= 25 ) age3o++
          else age3n++          
      } else if (val.calculated_age > 20) {
          if (val.calculated_bmi >= 30 ) age2b++
          else if (val.calculated_bmi >= 25 ) age2o++
          else age2n++
      } else {
          if (val.calculated_bmi >= 30 ) age1b++
          else if (val.calculated_bmi >= 25 ) age1o++
          else age1n++          
      }
      // For options 1 and 2
      if (val.gender == 'M') {
        genderM++;
        if (val.calculated_age > 60) {
            mo.push(val.calculated_bmi);
            mOld++;
            mOldTot = mOldTot + val.calculated_bmi;
        } else if (val.calculated_age > 40) {
            mm.push(val.calculated_bmi);
            mMed++;
            mMedTot = mMedTot + val.calculated_bmi;
        } else if (val.calculated_age > 20) {
            my.push(val.calculated_bmi);
            mYoung++;
            mYoungTot = mYoungTot + val.calculated_bmi;
        } else {
        	mc.push(val.calculated_bmi);
            mChild++;
            mChildTot = mChildTot + val.calculated_bmi;
        }

        if (val.ethnicity == 'Non-Hispanic Black') {
            ma.push(val.calculated_bmi);
        } else if (val.ethnicity == 'Mexican American') {
            mh.push(val.calculated_bmi);
        } else {
            mw.push(val.calculated_bmi);
        }        

      } else {
        genderF++;

        if (val.calculated_age > 60) {
            fo.push(val.calculated_bmi);
            fOld++;
            fOldTot = fOldTot + val.calculated_bmi;
        } else if (val.calculated_age > 40) {
            fm.push(val.calculated_bmi);
            fMed++;
            fMedTot = fMedTot + val.calculated_bmi;
        } else if (val.calculated_age > 20) {
			fy.push(val.calculated_bmi);
            fYoung++;
            fYoungTot = fYoungTot + val.calculated_bmi;
        } else {
        	fc.push(val.calculated_bmi);
            fChild++;
            fChildTot = fChildTot + val.calculated_bmi;
        }

        if (val.ethnicity == 'Non-Hispanic Black') {
            fa.push(val.calculated_bmi);
        } else if (val.ethnicity == 'Mexican American') {
            fh.push(val.calculated_bmi);
        } else {
            fw.push(val.calculated_bmi);
        }   


      };
    });
    
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
   
      if(values.length % 2) {
          medianSet = values[half];
          lowQ = (values[low-1] + values[low]) / 2.0;
          highQ = (values[high-1] + values[high]) / 2.0;
          minSet = values[low5];
          maxSet = values[high5];
      } else {
          medianSet = (values[half-1] + values[half]) / 2.0;
          lowQ = values[low];
          highQ = values[high];
          minSet = (values[low5-1] + values[low5]) / 2.0;
          maxSet = (values[high5-1] + values[high5]) / 2.0;
      }



      //minSet = values[0];
      //maxSet = values[values.length-1];

      var returnSet = [minSet,lowQ,medianSet,highQ,maxSet];
      return returnSet;
    }

    var age1 = age1n + age1o + age1b;
    var age2 = age2n + age2o + age2b;
    var age3 = age3n + age3o + age3b;
    var age4 = age4n + age4o + age4b;
    
    options0.series.push({
      name: "Obese",
      data: [(age1b/age1),(age2b/age2),(age3b/age3),(age4b/age4)]
    },{
      name: "Overweight",
      data: [(age1o/age1),(age2o/age2),(age3o/age3),(age4o/age4)]
    },{
      name: "Normal",
      data: [(age1n/age1),(age2n/age2),(age3n/age3),(age4n/age4)]
    });
    options1.series.push({
      name: "Female",
      data: [{y: (fChildTot/fChild), dataCount: fChild},{y: (fYoungTot/fYoung), dataCount: fYoung},{y: (fMedTot/fMed), dataCount: fMed},{y: (fOldTot/fOld), dataCount: fOld}]
    },{
      name: "Male",
      data: [{y: (mChildTot/mChild), dataCount: mChild},{y: (mYoungTot/mYoung), dataCount: mYoung},{y: (mMedTot/mMed), dataCount: mMed},{y: (mOldTot/mOld), dataCount: mOld}]
    });
    options2.series.push({
      name: "Female",
      data: [
        median(fc),median(fy),median(fm),median(fo)
      ]
    },{
      name: "Male",
      data: [
        median(mc),median(my),median(mm),median(mo)
      ]
    });

    options3.series.push({
      name: "African-American",
      data: [
        median(fa),median(ma)
      ]
    },{
      name: "Hispanic",
      data: [
        median(fh),median(mh)
      ]
    },{
      name: "White",
      data: [
        median(fw),median(mw)
      ]
    });


    //console.log(options1);
    //console.log(options2);

    var chart0 = new Highcharts.Chart(options0);
    var chart1 = new Highcharts.Chart(options1);
    var chart2 = new Highcharts.Chart(options2);
    var chart3 = new Highcharts.Chart(options3);

    console.log(options2);
  });
  



}); // $(document).ready(function() {