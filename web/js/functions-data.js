$(document).ready(function() {

  var colorArray = ['#AA4643', '#4572A7', '#89A54E', '#80699B', '#3D96AE', '#DB843D', '#92A8CD', '#A47D7C', '#B5CA92'];
  

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
      min: 25,
      max: 30,
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
      min: 15,
      max: 45,
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
      min: 17,
      max: 42,
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

var options4 = {
    chart: {
      renderTo: 'container4',
      type: 'boxplot',
      plotBackgroundColor: '#fffdf6'
    },
    colors: colorArray,
    title: {
      //text: 'King County - BMI by Ethnicity'
    },    
    tooltip: {
	    useHTML: true,
	    headFormat: '{point.key} - {series.name}',
	    pointFormat: '95th quantile: {point.high}<br />Upper quartile: {point.q3}<br />Median: {point.median}<br />Lower quartile: {point.q1}<br />5th quantile: {point.low}'
	 },
    xAxis: {
        categories: [
        //    'Female','Male'
        ]
    },
    yAxis: {
      title: {
        text: 'BMI'
      },
      min: 17,
      max: 42,
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
  var fn = [];
  var ma = [];
  var mh = [];
  var mw = [];
  var mn = [];

  $.getJSON('../data/data5000.json', function(data) {
    $.each(data, function(key, val) {
      
      // For options 1 and 2
      if (val.Gender == 'M') {
        genderM++;
        if (val.Calculated_Age > 60) {
            mo.push(val.Calculated_BMI);
            mOld++;
            mOldTot = mOldTot + val.Calculated_BMI;
        } else if (val.Calculated_Age > 40) {
            mm.push(val.Calculated_BMI);
            mMed++;
            mMedTot = mMedTot + val.Calculated_BMI;
        } else if (val.Calculated_Age > 20) {
            my.push(val.Calculated_BMI);
            mYoung++;
            mYoungTot = mYoungTot + val.Calculated_BMI;
        } else {
            mc.push(val.Calculated_BMI);
            mChild++;
            mChildTot = mChildTot + val.Calculated_BMI;
        }

        if (val.Ethnicity == 'Black') {
            ma.push(val.Calculated_BMI);
        } else if (val.Ethnicity == 'Mexican American') {
            mh.push(val.Calculated_BMI);
        } else if (val.Ethnicity == 'White') {
            mw.push(val.Calculated_BMI);
        } else {
            mn.push(val.Calculated_BMI);
        }        

      } else {
        genderF++;

        if (val.Calculated_Age > 60) {
            fo.push(val.Calculated_BMI);
            fOld++;
            fOldTot = fOldTot + val.Calculated_BMI;
        } else if (val.Calculated_Age > 40) {
            fm.push(val.Calculated_BMI);
            fMed++;
            fMedTot = fMedTot + val.Calculated_BMI;
        } else if (val.Calculated_Age > 20) {
			fy.push(val.Calculated_BMI);
            fYoung++;
            fYoungTot = fYoungTot + val.Calculated_BMI;
        } else {
        	fc.push(val.Calculated_BMI);
            fChild++;
            fChildTot = fChildTot + val.Calculated_BMI;
        }

        if (val.Ethnicity == 'Black') {
            fa.push(val.Calculated_BMI);
        } else if (val.Ethnicity == 'Mexican American') {
            fh.push(val.Calculated_BMI);
        } else if (val.Ethnicity == 'White') {
            fw.push(val.Calculated_BMI);
        } else {
            fn.push(val.Calculated_BMI);
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
    },{
      name: "Other",
      data: [
        median(fn),median(mn)
      ]
    });
    
    //console.log(options1);
    //console.log(options2);

    var chart1 = new Highcharts.Chart(options1);
    var chart2 = new Highcharts.Chart(options2);
    var chart3 = new Highcharts.Chart(options3);

var chart4;
var goat = false;
$("#xAxisSelect").on("change", function(){
    if (goat) {
        var goo = options4.series.length;
        console.log(goo);
        options4.series[0].remove;
        options4.series[1].remove;
        options4.series[2].remove;
        options4.series[3].remove;
    }
      options4.title.text = "King County BMI by " + this.value;
      if (this.value == "Gender") {
          options4.xAxis.categories = ['Female','Male'];
            options4.series.push({
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
            },{
              name: "Other",
              data: [
                median(fn),median(mn)
              ]
            });          
 
      } else {
          options4.xAxis.categories = ["African-American","Hispanic","White","Other"];
            options4.series.push({
              name: "Female",
              data: [
                median(fa),median(fh),median(fw),median(fn)
              ]
            },{
              name: "Male",
              data: [
                median(ma),median(mh),median(mw),median(mn)
              ]
            });          
      }
      chart4 = new Highcharts.Chart(options4);
      goat = true;
      console.log(chart4.series);
  })
  
  });
  

  


}); // $(document).ready(function() {