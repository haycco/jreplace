$(function () { 
    //显示消息
    function showMessage(message) { 
        $('#messageDialog').dialog('open');
        $('#messageDialog .msg').html(message);
    }
    //显示人员详细信息
    function showPersonDetail() {
        var name = $('#personsTable').jqGrid('getGridParam', 'selrow');
        if (name) {
            $.ajax({
                type : "Get",
                url : "/jquery-training/person?operation=getPersonByName&name=" + name,
                dataType : "json",
                cache : false,
                success : function (data) {
                    if (data) {
                        $("#personName").html(data.name);
                        $("#personSex").html(data.sex);
                        $("#personAge").html(data.age);
                        $("#personStation").html(data.station);
                        $('#personDetailDialog').dialog("open"); 
                    }
                },
                error : function(err) {
                    showMessage("获取人员详细信息错误!");
                }
            });
        } else {
            showMessage("请选择要查看的人员！");
        }
    }
    
    //删除人员信息
    function deletePerson() {
        var name = $('#personsTable').jqGrid('getGridParam', 'selrow');
        if (name) {
            $.ajax({
                type : "Get",
                url : "/jquery-training/person?operation=deletePerson&name=" + name,
                dataType : "json",
                cache : false,
                success : function(data) {
                    if (data) {
                        $('#personsTable').jqGrid('delRowData', name);
                    }
                },
                error : function(err) {
                    showMessage("删除人员信息错误!");
                }
            });
        } else {
            showMessage("请选择要删除的人员！");
        }
    }
    
    //获取所有人员信息
    function getPersons() {
        $.ajax({
            type : "Get",
            url : "/jquery-training/person?operation=getPersons",
            dataType : "json",
            cache : false,
            success : function(data) {
                if (data && data.length > 0) {
                    $('#personsTable').jqGrid('clearGridData');
                    $.each(data, function(index, rowData) {
                        $('#personsTable').jqGrid('addRowData', rowData.name, {
                            name:rowData.name,
                            sex:rowData.sex,
                            age:rowData.age,
                            station:rowData.station,
                        }, 'first');
                    })
                }
            },
            error : function(err) {
                showMessage("获取所有人员信息错误!");
            }
        });
    }

    //登录
    function login() {
        var username = $("#username").val();
        var password = $("#password").val();
        if (!username) {
            showMessage("请输入登录用户名!");
            return;
        }
        if (!password) {
            showMessage("请输入登录密码!");
            return;
        }
        $.ajax({
            type : "Get",
            url : "/jquery-training/login?username=" + username + "&password=" + password,
            dataType : "json",
            cache: false,
            success : function(data) {
                if (data) {
                    $('#loginDialog').dialog("close");
                    $('#messageDialog').dialog("close"); 
                    $('#personsTable').jqGrid("setGridWidth", '960');
                    $('#welcomeContent').css("display", "block");
                    
                } else {
                    showMessage("登录失败!请输入正确的用户名和密码!");
                }
            },
            error : function(err) {
                showMessage("登录错误!");
            }
        });
    }
    
    //点击'获取人员信息'按钮
    $("#getPersonsButton").click(function() {
        getPersons();
        return false;
    });
    
    //点击'删除人员信息'按钮
    $("#deletePersonButton").click(function() {
        deletePerson();
        return false;
    });
    
    //点击'显示详细信息'按钮
    $("#showDetailButton").click(function() {
        showPersonDetail();
        return false;
    });
    
    // 登录框
    $('#loginDialog').dialog({
        title: "登录框",
        autoOpen: false,
        width: 300,
        modal:true,
        resizable:false,
        closeOnEscape:false,
        buttons: [{
            id: 'loginButton',
            text: "登录",
            click: function() {
                login();
                return false;
            }
        },
        {
            id: 'cancelLoginButton',
            text: "取消",
            click: function() { 
                $('#loginDialog').dialog("close"); 
                return false;
            }
        }]
    });
    $('#loginDialog').keydown(function(event){
        if (event.keyCode == 13) {
            login();
        }
    });
    $('#loginDialog').dialog("open");
    
    //消息框            
    $('#messageDialog').dialog({
        title: "信息框",
        autoOpen: false,
        width: 300,
        modal:true,
        resizable:false,
        buttons: {
                    "确定": function() { 
                        $('#messageDialog').dialog("close"); 
                        return false;
                    } 
                 }
    });
    
    //组件列表
    $('#personsTable').jqGrid({
        height:'320',
        datatype:'json',
        colNames:['姓名', '性别', '年龄', '岗位'],
        colModel:[{name:'name', index:'name', width:'100', sortable:false},
                  {name:'sex', index:'sex', width:'100', sortable:false},
                  {name:'age', index:'age', width:'100', sortable:false},
                  {name:'station', index:'station', width:'100', sortable:false}],
        caption:'人员列表',
        viewrecords:true,
        cellEdit:false,
        loadtext:'数据加载中...',
        rowNum:15,
        autowidth:true,
        sortname:'name',
        forceFit:true,
        altRows:true,
        multiselect:false,
        ondblClickRow:function() {
            showPersonDetail();
        }
    });

    //详细信息框            
    $('#personDetailDialog').dialog({
        title: "详细信息框",
        autoOpen: false,
        width: 280,
        height: 320,
        modal:true,
        resizable:false,
        buttons: {
                    "确定": function() { 
                        $('#personDetailDialog').dialog("close"); 
                        return false;
                    }
                 }
    });
    
});