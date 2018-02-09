var exec = require('cordova/exec');

function getPreventTouchAreaParameter(touchAreas){
    var touchAreasObj=[];
    for(var i=0;i<touchAreas.length;i++){
        touchAreasObj.push(getPoint(touchAreas[i]))
    }
    return touchAreasObj;
}
function getPoint(touchAreas){
    //XY代表坐标系的X负Y正 X1正Y1负
    var y = touchAreas.offsetTop;
    var x = touchAreas.offsetLeft;
    var y1= y+touchAreas.offsetHeight;
    var x1= x+touchAreas.offsetWidth;
    while(touchAreas = touchAreas.offsetParent){
         y += touchAreas.offsetTop;
         x += touchAreas.offsetLeft;
    }
    return {X:x.toString(),Y:y.toString(),X1:x1.toString(),Y1:y1.toString()}
}
function getclientpx(){
    return{
        h:window.screen.height.toString(),
        w:window.screen.width.toString()
    }
}

exports.getMap = function (success, error) {
    //获取禁止触摸事件分发的区域需要设置namap-prevent类
    var touchAreas = document.getElementsByClassName('namap-prevent');
    //生成静止触摸区域对象
    console.log(touchAreas,touchAreas.length);
    
    if(touchAreas.length!=0){
        var touchAreaObj=[
            getPreventTouchAreaParameter(touchAreas),
            getclientpx()
        ]
        exec(success, error, 'NAmap', 'getMap',touchAreaObj);
    }else{
        console.info("namap cannot find prevent touch area!");
        exec(success, error, 'NAmap', 'getMap',[1]);
    }
    
};
