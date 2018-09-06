var base_url = "http://prestariang.akaunsaya.com:5000";
var url_multiple = base_url + "/vehicleLocationHistory?deviceid=";
var url_websocket = base_url + "/gs-guide-websocket";
var url_devices = base_url + "/vehicleLocationHistoryLatest";
var stompClient = null;
$("#rightside").hide();
var markers = [];
var popups = [];
var timers = [];
var count = 0;
connect();
loadDevice();




function connect() {
    var socket = new SockJS(url_websocket);
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {

        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/vehicleTracking', function (greeting) {

            var output = JSON.parse(greeting.body);

            if(output.deviceID == $('#deviceid').val() || $('#deviceid').val() == 'all'){
                showRealtimeMap(output);
            }

        });
    });
}
function loadDevice()
{
    var colorNames = [
        "#b40219",
        "#9db42d",
        "#2fb455",
        "#2452b4",
        "#9253b4",
        "#06b4a8",
        "#e5efb0",
        "#b66d42",
        "#6c690b",
        "#09570f",
        "#82ee05"
    ];

    $('#deviceid')
        .find('option')
        .remove()
        .end()
        .append('<option value="all">All device</option>')
        .val('all');

    $.getJSON( url_devices, function( data ) {
        markers = [];
        popups = [];
        var markerIndex = 0;

        $.each( data, function( key, val ) {
            console.log(val);
            ++markerIndex;
            var el = document.createElement('div');

            el.backgroundColor = "FF0000";
            el.color = colorNames[markerIndex];
            /*var marker = new mapboxgl.Marker(el, {
                offset: [0, -17.5]

            })*/



            addPopup(val.geometry.coordinates[1],val.geometry.coordinates[0],val.deviceID);

            var marker = new mapboxgl.Marker({ "color": colorNames[markerIndex]})
                .setLngLat([val.geometry.coordinates[1], val.geometry.coordinates[0]])
                .addTo(map);


            marker.color = colorNames[markerIndex];
            marker.name = val.deviceID;
            markers.push(marker);

            $('#deviceid').append( new Option(val.deviceID,val.deviceID,false,false) );
        });

        var deviceIDURL = getQueryParam('deviceid');

        //for lutfi iframe hack
        if(deviceIDURL != 'deviceid')
        {
            $('#deviceid').val(deviceIDURL).change();
        }

    });


}

function addPopup(long,lat,deviceid)
{
    var popup = new mapboxgl.Popup({closeOnClick: false, offset : 40})
        .setLngLat([long, lat])
        .setHTML(deviceid)
        .addTo(map);
    popups.push(popup);
}

function showRealtimeMap(message) {

    console.log("function showRealtimeMap(message) {");
    console.log(message);

    var Lat = message.geometry.coordinates[0];
    var Long = message.geometry.coordinates[1];

    console.log(Lat,Long);
    /*    marker.setLngLat([
            parseFloat(Long),
            parseFloat(Lat)
        ]);

        map.flyTo({center: [
                parseFloat(Long),
                parseFloat(Lat)
            ]});*/

    //requestAnimationFrame(showGreeting);
    popups.forEach(x => {
        x.remove();
});
    markers.forEach( (x,y) => {
        $(x.getElement()).hide();

    console.log("Hiding " + x.name);

    if(x.name == message.deviceID){

        console.log("Showing " + x.name);
        $(x.getElement()).show();

        markers[y].setLngLat([
            parseFloat(Long),
            parseFloat(Lat)
        ]);
        addPopup(x._lngLat.lng,x._lngLat.lat,x.name);
        map.flyTo({center: [
                x._lngLat.lng,
                x._lngLat.lat
            ]});
    }
});


}

mapboxgl.accessToken = 'pk.eyJ1IjoiaGFzc2FucHJlc3RhcmlhbmciLCJhIjoiY2psMXA4aWFiMWkxNDNwcXRidXJ4NGpwNCJ9.ZILPPxFgkleK1ica54v4fA';
var map = new mapboxgl.Map({
    container: 'map', // container id
    //style: 'mapbox://styles/mapbox/basic-v9',
    style: 'mapbox://styles/mapbox/dark-v9',
    center: [101.6519444, 2.9094875], // starting position
    zoom: 15 // starting zoom
});

// Add zoom and rotation controls to the map.
map.addControl(new mapboxgl.NavigationControl());
// Add geolocate control to the map.
map.addControl(new mapboxgl.GeolocateControl({
    positionOptions: {
        enableHighAccuracy: true
    },
    trackUserLocation: true
}));
//var marker = new mapboxgl.Marker();

/*function animateMarker(timestamp) {
    var radius = 20;

    // Update the data to a new position based on the animation timestamp. The
    // divisor in the expression `timestamp / 1000` controls the animation speed.
    marker.setLngLat([
        101.6519444,
        2.9094875
    ]);

    // Ensure it's added to the map. This is safe to call if it's already added.
    marker.addTo(map);

    // Request the next frame of the animation.
    //requestAnimationFrame(animateMarker);
}*/

// zaid's code to add polygon layer
$.ajaxSetup({
    async: false
});



function addlayers() {
    if (map.getLayer('Star Central Polygon')) {

    } else {
        map.addLayer({
            'id': 'Star Central Polygon',
            'type': 'fill',
            'source': {
                'type': 'geojson',
                'data': {
                    'type': 'Feature',
                    'geometry': {
                        'type': 'Polygon',
                        'coordinates':
                            [
                                [
                                    [101.653170, 2.911660],
                                    [101.651540, 2.911660],
                                    [101.651540, 2.913460],
                                    [101.652331, 2.913460],
                                    [101.652331, 2.912460],
                                    [101.653170, 2.912460],
                                    [101.653170, 2.911660]
                                ]
                            ]
                    }
                }
            },
            'layout': {},
            'paint': {
                'fill-color': 'yellow',
                'fill-opacity': 0.25
            }
        });


        map.addLayer({
            'id': 'Star Central Building',
            'type': 'fill-extrusion',
            'source': {
                'type': 'geojson',
                'data': data_star_central
            },
            'paint': {
                // Get the fill-extrusion-color from the source 'color' property.
                'fill-extrusion-color': ['get', 'color'],

                // Get fill-extrusion-height from the source 'height' property.
                'fill-extrusion-height': ['get', 'height'],

                // Get fill-extrusion-base from the source 'base_height' property.
                'fill-extrusion-base': ['get', 'base_height'],

                // Make extrusions slightly opaque for see through indoor walls.
                'fill-extrusion-opacity': 0.5
            }
        });
    }
}

function addHeatmap(device_id) {
    var jsonData = {
        "type": "FeatureCollection",
        "crs": {
            "type": "name",
            "properties": { "name": "urn:ogc:def:crs:OGC:1.3:CRS84" }
        },
        "features": (function() {
            var result;
            $.getJSON('http://prestariang.akaunsaya.com:5000/vehicleLocationHistory?deviceid='+device_id, {}, function(data){
                result = data;
            });
            return result;
        })()
    };
    console.log(jsonData.features);

    $.each( jsonData.features, function( key, val ) {
        var ori = val.geometry.coordinates;
        var newer = [ori[1], ori[0]];
        val.geometry.coordinates = newer;
    });

    if (map.getLayer('Device Heatmap')) {
        map.getSource('deviceheatdata').setData(jsonData);
    } else {
        map.addSource('deviceheatdata', {
            "type": "geojson",
            "data": jsonData
        });

        map.addLayer({
            "id": "Device Heatmap",
            "type": "heatmap",
            "source": "deviceheatdata",
            "maxzoom": 18
        }, 'waterway-label');

        map.addLayer({
            "id": "Device Point",
            "type": "circle",
            "source": "deviceheatdata",
            "minzoom": 7,
            "paint": {
                // Size circle radius by earthquake magnitude and zoom level
                "circle-radius": [
                    "interpolate",
                    ["linear"],
                    ["zoom"],
                    7, 1,//[
                    //    "interpolate",
                    //    ["linear"],
                    //    ["get", "mag"],
                    //    1, 1,
                    //    6, 4
                    //],
                    16, 1 //[
                    //    "interpolate",
                    //    ["linear"],
                    //   ["get", "mag"],
                    //    1, 5,
                    //    6, 50
                    //]
                ],
                // Color circle by earthquake magnitude
                "circle-color": [
                    "interpolate",
                    ["linear"],
                    1,
                    //["get", "mag"],
                    1, "rgba(33,102,172,0)",
                    2, "rgb(103,169,207)",
                    3, "rgb(209,229,240)",
                    4, "rgb(253,219,199)",
                    5, "rgb(239,138,98)",
                    6, "rgb(178,24,43)"
                ],
                "circle-stroke-color": "white",
                "circle-stroke-width": 1,
                // Transition from heatmap to circle layer by zoom level
                "circle-opacity": [
                    "interpolate",
                    ["linear"],
                    ["zoom"],
                    7, 0,
                    8, 1
                ]
            }
        }, 'waterway-label');

        insideMapMenu([ 'Star Central Polygon', 'Star Central Building', 'Device Heatmap', 'Device Point' ]);
    }
}

function insideMapMenu(menu_list) {
    var toggleableLayerIds = menu_list;
    $('#menu_2').html('');
    for (var i = 0; i < toggleableLayerIds.length; i++) {
        var id = toggleableLayerIds[i];

        var link = document.createElement('a');
        link.href = '#';
        link.className = 'active';
        link.textContent = id;

        link.onclick = function (e) {
            var clickedLayer = this.textContent;
            e.preventDefault();
            e.stopPropagation();

            var visibility = map.getLayoutProperty(clickedLayer, 'visibility');

            if (visibility === 'visible') {
                map.setLayoutProperty(clickedLayer, 'visibility', 'none');
                this.className = '';
            } else {
                this.className = 'active';
                map.setLayoutProperty(clickedLayer, 'visibility', 'visible');
            }
        };
        var layers = document.getElementById('menu_2');
        layers.appendChild(link);

        
    }
}

map.on('load', function() {
    addlayers();
    insideMapMenu([ 'Star Central Polygon', 'Star Central Building']);
});

map.on('style.load', function () {
    // Triggered when `setStyle` is called.
    addlayers();
});

var timer;
var berhenti = false;

$("#stop").click(function(){
    console.log(timer);
    berhenti = true;
    clearTimeout(timer);
    /* if (timer) {

         timer = 0;
     }*/
});
// Start the animation.
//requestAnimationFrame(animateMarker);
function replay(terminator = false,item,index,countParam,marker,total_count)
{
    //console.log("replay" + terminator);

    count++;

    marker.setLngLat([
        item.geometry.y,
        item.geometry.x
    ]);
    //console.log(item);
    popups.forEach(x => {
        //console.log("popup " + x);
        x.remove();
});
    popups = [];
    addPopup(item.geometry.y,item.geometry.x,item.deviceID);
    map.flyTo({center: [
            item.geometry.y,
            item.geometry.x
        ]});

    var timeToShow = moment(item.properties.timestamp).format("D/M/Y hh:mm:ss");

    $("#time").text(timeToShow);
    $("#countdown").text(count + '/' + total_count);




    // https://stackoverflow.com/questions/37977602/settimeout-not-working-inside-foreach
}

function dropdownChange()
{
    berhenti = false;
    var mode = $("#mode").val();
    console.log(mode)
    clearTimeout(timer);
    markers.forEach(x => {
        console.log("Hiding " + x.name);
    $(x.getElement()).hide();
});

    popups.forEach(x => {
        console.log("popup " + x);
    x.remove();
});

    if(mode == 'replay') {

        if($('#deviceid').val() == 'all'){

        } else {

            $("#rightside").show();

            $.getJSON( url_multiple + $('#deviceid').val(), function( data ) {
                var finalData = [];

                $.each( data, function( key, val ) {
                    finalData.push(val);
                });

                count = 0;
                var total_count = finalData.length;

                marker = accessMarkers($('#deviceid').val());

                $(marker.getElement()).show();

                addPopup(marker._lngLat.lng,marker._lngLat.lat,marker.name);
                map.flyTo({center: [
                        marker._lngLat.lng,
                        marker._lngLat.lat
                    ]});
                console.log(marker);

                /*for(i = 0; i < finalData.length ; i++)
                {
                    //console.log(finalData[i])
                    //replay(false, item, index, count,marker,total_count);
                    /!*timer = setTimeout(function() {

                    },200);*!/
                    //if(stopTimer > 1) retu
                    setTimeout(function() {
                        replay(false, finalData[i], i, count, marker, total_count);
                    },1000);
                }*/

                (function theLoop (i,counter,marker) {
                    console.log(i);
                    console.log(counter);
                    timer = setTimeout(function () {
                        //alert("Cheese!");
                        replay(false, finalData[counter], i, count, marker, total_count);
                        if (--i) {          // If i > 0, keep going
                            //replay2(false, finalData[i], i, count, marker, total_count);
                            theLoop(i,counter,marker);
                        }
                    }, 1500);
                    counter++;

                })(finalData.length,0,marker);


                /*finalData.forEach((item,index) => {
                    console.log(index)
                });*/

            });
        }
    } else if(mode == "realtime") {

        $("#rightside").hide();
        connect();

        if($('#deviceid').val() == 'all') {

            //loadDevice();

            markers.forEach(x => {
                $(x.getElement()).show();
            addPopup(x._lngLat.lng,x._lngLat.lat,x.name);
        })

        } else {


            markers.forEach(x => {
                console.log("Hiding " + x.name);
            $(x.getElement()).hide();

            if(x.name == $('#deviceid').val()){
                console.log("Showing " + x.name);
                $(x.getElement()).show();
                addPopup(x._lngLat.lng,x._lngLat.lat,x.name);
                map.flyTo({center: [
                        x._lngLat.lng,
                        x._lngLat.lat
                    ]});
                addHeatmap($('#deviceid').val());
            } else {

            }
        })
        }

    } else { console.log("Something is wrong") }
}
$("#mode").change(function(){
    console.log(markers);
    dropdownChange();
});

$("#deviceid").change(function(){
    console.log(markers);
    dropdownChange();
});

function accessMarkers(deviceid)
{
    for(i=0;markers.length;i++){
        if(markers[i].name == deviceid) {
            return markers[i];
        }
    }
}

$("#mapbox_style").change(function(){

    var layerId = $('#mapbox_style').val();
    map.setStyle('mapbox://styles/mapbox/' + layerId + '-v9');

});

function getQueryParam(param) {
    location.search.substr(1)
        .split("&")
        .some(function(item) { // returns first occurence and stops
            return item.split("=")[0] == param && (param = item.split("=")[1])
        })
    return param
}