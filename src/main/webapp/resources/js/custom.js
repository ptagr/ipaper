var staticrow = 9;

function loadXMLDoc() {
		
		
		refreshPapers();
		
		
		$(function() {
			var d = new Date();
			var gmtHours = -d.getTimezoneOffset() / 60;
			MinToAdd = (5.50 - gmtHours) * 60;
			var maxDate = new Date(d.getTime() + MinToAdd * 60000);
			/*
			minDate = new Date(maxDate.getTime());
			for (i = 0; i < 7 * 24; i++) {
				minDate = new Date(minDate.getTime() - 60 * 60000);
			}*/
			$("#datepicker").datepicker({
				beforeShowDay: noSunday,
				maxDate : maxDate,
				dateFormat : 'ddmmyy',
				defaultDate: maxDate,
				showAnim: 'slide'
			});
			$("#datepicker").val($.datepicker.formatDate('ddmmyy', maxDate));
		});
		
		

	}

	function addOption(selectbox, text, value) {
		var optn = document.createElement("OPTION");
		optn.text = text;
		optn.value = value;
		optn.id = value;
		selectbox.options.add(optn);
	}

	function removeAllOptions(selectbox) {
		var i;
		for (i = selectbox.options.length - 1; i >= 0; i--) {
			selectbox.remove(i);
		}
	}

	function refreshcities(value0, value) {
		if(value == "")
			return;
		var DDCities = document.getElementById('DDCities');
		var responsetxt = $.ajax({
			url : "http://ipaper.cloudfoundry.com/epaper/" + value0
					+ "/"+ value + "/supportedcities.json",
			async : false
		}).responseText;
		cities = jQuery.parseJSON(responsetxt);
		removeAllOptions(DDCities);
		for ( var i = 0; i < cities.length; i++) {
			addOption(DDCities, cities[i], cities[i]);
		}
	}

	
	
	function refreshLanguages(value) {
		if(value == "")
			return;
		var DDlanguages = document.getElementById('DDlanguages');
		var responsetxt = $
				.ajax({
					url : "http://ipaper.cloudfoundry.com/epaper/"+ value +"/supportedlanguages.json",
					async : false
				}).responseText;
		languages = jQuery.parseJSON(responsetxt);

		// bind change event to DDLanguages
		$("#DDlanguages").bind("change", function(event) {
			refreshcities(event.target.value);
		});
		removeAllOptions(DDlanguages);
		for ( var i = 0; i < languages.length; i++) {
			addOption(DDlanguages, languages[i], languages[i]);
		}
		
		$("#DDlanguages").val(languages[0]);
		refreshcities(value,languages[0]);
	}
	
	
	function refreshPapers() {

		var DDpapers = document.getElementById('DDpapers');
		var responsetxt = $
				.ajax({
					url : "http://ipaper.cloudfoundry.com/epaper/supportedpapers.json",
					async : false
				}).responseText;
		papers = jQuery.parseJSON(responsetxt);

		// bind change event to DDLanguages
		$("#DDpapers").bind("change", function(event) {
			refreshLanguages(event.target.value);
		});
		//removeAllOptions(DDlanguages);
		for ( var i = 0; i < papers.length; i++) {
			addOption(DDpapers, papers[i], papers[i]);
		}
		
		$("#DDpapers").val(papers[0]);
		refreshLanguages(papers[0]);

	}

	function getPaper() {
		$('#getPaperButton').attr("disabled", "disabled");
		var paper = document.getElementById('DDpapers').value;
		var language = document.getElementById('DDlanguages').value;
		var city = document.getElementById('DDCities').value;
		var date = document.getElementById('datepicker').value;
		if (date != "") {
			varurl = "http://ipaper.cloudfoundry.com/epaper/" + paper + "/" + date
					+ "/" + city + "/" + language + ".json";
		}

		barStart();
		var mytable = document.getElementById('mytable');
		while (mytable.rows.length > staticrow) {
			mytable.deleteRow(staticrow);
		} 
		var mytable = document.getElementById('mytable');					
		var tfoot = mytable.getElementsByTagName("TFOOT").item(0);
		var row = document.createElement("TR");
		var td = document.createElement("TH");
		td.setAttribute('colspan','2');
		var img = document.createTextNode("Epaper link will appear here when indicator stops");
		td.appendChild(img);
		row.appendChild(td);
		tfoot.appendChild(row);
		
		$.ajax({
			url : varurl,
			dataType : 'json',
			success : function(data) {
				paper = data;
				if (paper != null && typeof paper != "undefined") {
					barStop();
					window.open(paper.url,'_blank');
					var mytable = document.getElementById('mytable');
					while (mytable.rows.length > staticrow) {
						mytable.deleteRow(staticrow);
					}
					var tfoot = mytable.getElementsByTagName("TFOOT").item(0);
					var row = document.createElement("TR");
					var td = document.createElement("TH");
					var newlink = document.createElement('a'); 
					newlink.setAttribute('href',paper.url);
					var img = document.createTextNode("CLick here to get the epaper <br>. Link expires after "+paper.expiryTimeInterval/60+" mins");
					newlink.appendChild(img);
					td.appendChild(newlink);
					td.setAttribute('colspan','2');
					row.appendChild(td);
					tfoot.appendChild(row);

					/*var mytable = document.getElementById('mytable');
					var rowCount = mytable.rows.length;
					for ( var i = 0; i < rowCount; i++) {
						mytable.deleteRow(i);
					}
					var tbody = mytable.getElementsByTagName("TBODY").item(0);
					var row = document.createElement("TR");
					for ( var i = 0; i < papers.length; i++) {
						var td = document.createElement("TD");
						var img = document.createElement("img");
						img.src = papers[i].thumbnailUrl;
						img.onclick = Function("window.open('" + papers[i].url
								+ "');");
						img.width = '300';
						var ahref = document.createElement("a");
						anchortext = document.createTextNode("Download Paper");
						ahref.appendChild(anchortext);
						ahref.align = "center";
						ahref.href = papers[i].url;
						td.appendChild(img);
						td.appendChild(document.createElement("br"));
						td.appendChild(ahref);
						row.appendChild(td);
					}

					tbody.appendChild(row);*/
					$('#getPaperButton').removeAttr('disabled');
				}
			},
			error : function(textStatus) {
				if (textStatus.statusText == "Gateway Time-out") {
					setTimeout("barStop();getPaper()",10000);
					//barStop();
					//getPaper();
				} else {
					barStop();
					var mytable = document.getElementById('mytable');
					while (mytable.rows.length > staticrow) {
						mytable.deleteRow(staticrow);
					}
					var tfoot = mytable.getElementsByTagName("TFOOT").item(0);
					var row = document.createElement("TR");
					var td = document.createElement("TH");
					var img = document.createTextNode("An unexpected error occured :( Try again !!");
					td.appendChild(img);
					td.setAttribute('colspan','2');
					row.appendChild(td);
					tfoot.appendChild(row);
				}
				$('#getPaperButton').removeAttr('disabled');

			}

		});

	}


	
	function barStop(){
		for(var i=0;i<5;i++)
			bar.toggleStop();
	}
	
	function barStart(){
		bar.toggleStart();
		bar.showBar();
	}
	
	function noSunday(date){
        var day = date.getDay();
                    return [(day > 0), ''];
    }; 