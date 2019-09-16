
var crypto = require('crypto');
var express = require('express');
var mysql = require('mysql');
var bodyParser = require('body-parser');
var post_get_user_id = 0;
var  Schedule_id_latest=0;
var post_user_email;
//connect to mysql
var con = mysql.createConnection({
    host:'localhost',
    user:'root',
    password:'12345678',
    database:'mynewdb'
});
var app = express();
app.use(bodyParser.json());// accept jason para
app.use(bodyParser.urlencoded({extended: true}));
// send email and password here / first it will check email if exist then compare password
app.post('/login/',(req,res,next)=>{

    var post_data = req.body;
    //extract email and password from request
    var email_address = post_data.email_address;
    var user_password = post_data.user_password;
    con.query('SELECT * FROM user where email_address=?',[email_address], function (err, result, fields) {

        con.query('error',function (error) {
            console.log('[MySql ERROR]',err);
        });
        if(result && result.length )
        {
           // var salt =  result[0].salt;//get salt of account if exists
            var get_user_password = result[0].user_password;
            console.log(result[0].email_address   +'got email is ');
                console.log(result[0].user_password   +'password got is ');
            //hash password from login request with salt in database
           
            if(get_user_password == user_password){
                res.end(JSON.stringify(result[0]))//if password is true,return all info of user
                console.log(result[0].email_address    + '        Email Matched');
                console.log(result[0].user_password    + '        Password Matched');
                console.log(result[0].user_id          + '        UserID after Matching email and password');
                post_get_user_id =  result[0].user_id;    // here user id from data is saved in a variable and other data will be fetched accordingly        
           
                post_user_email = result[0].email_address;
                console.log(post_get_user_id +'User id copied over here');
                res.end(JSON.stringify(' Password Matched'));
                
        }else
                res.end(JSON.stringify('Wrong Password'));
        }
        else{

            res.json('User not exists!!!');

        }
    });
})

app.post('/POST_Email_Sign_In_to_fetch_user_data_API',(req,res,next)=>{

    var post_data = req.body;
    //extract email and password from request
    post_user_email = post_data.email_address;
    console.log("Email got is " + post_user_email);
 
});

// if email and password match it will send related data to android app

app.get('/login_response_data', (req, res)=>{
    con.query('SELECT user_id,user_type,user_name,email_address,user_password,user_contact,user_status , location_name FROM user , location where   user.location_id=location.location_id and email_address=?',[post_user_email]  ,(err,rows,fields)=>{
        if (!err){

            
            console.log('Login response data is ');
            console.log(rows);
            res.send(rows);
        } else 
            console.log('Login response error '+err);
    })
})


//Email Checking in Local Database 
app.get('/For_login_check_Email_Local_DB_API', (req, res)=>{
    con.query('SELECT email_address FROM user ',(err,rows,fields)=>{
        if (!err){
            console.log('For_login_check_Email_Local_DB_API');
            console.log(rows);
            res.send(rows);
        } else 
            console.log(err);
    })
})

// Mobile Verification code ... send phones with email 
app.get('/For_user_Authentication_check_phone_Local_DB_API', (req, res)=>{
    con.query('SELECT user_id,email_address,user_contact  FROM user ',(err,rows,fields)=>{
        if (!err){
            console.log('For_user_Authentication_check_phone_Local_DB_API');
            console.log(rows);
            res.send(rows);
        } else 
            console.log(err);
    })
})

// here to add a schedules user enter some values in android app and all values will be get here and used
app.post('/Add_Schedule_to_DB_API',(req,res,next)=>{
    console.log('Add_Schedule_to_DB_API');
    var post_data = req.body;//get POST params
    var  user_id = post_data.user_id;
         console.log(' user id from app                  '+post_data.user_id);
    var device_id = post_data.device_id;
    var time_before = post_data.time_before;
    var time_after = post_data.time_after;
    var schedule_action= post_data.schedule_action;
    if(schedule_action==1){
        var schedule_name = "Device_ON";
    }else{
        var schedule_name = "Device_OFF";
    }
    var schedule_status = 1;
    console.log(schedule_name);
            con.query('INSERT INTO schedule(user_id,device_id,schedule_name,time_before,time_after,schedule_action,schedule_status,add_schedule_time) VALUES(?,?,?,?,?,?,?,NOW())',[user_id,device_id,schedule_name,time_before,time_after,schedule_action,schedule_status],function (err, result, field) {
                if(err){
                    console.log('[MySql ERROR]',err);
                    console.log(schedule_name);
                    res.json('Schedule error: ',err);
                    console.log('Register Error');
                    res.end();
                };
                res.json('Schedule Added Successfully');
                console.log('Schedule Added  Successfully');
                res.end();
            });
        });




/// Delete schedule on basis of user_id and device id

        app.post('/Delete_schedule_using_device_user_id',(req,res,next)=>{
            console.log('Delete_schedule_using_device_user_id');
            var post_data = req.body;//get POST params
            var  user_id = post_data.user_id;
            
            var device_id = post_data.device_id;
           
           // console.log(' user id from app                  '+user_id+'.....'+device_id) ;
            //console.log("DELETE  FROM  schedule WHERE user_id= "+user_id +"  and device_id= "+device_id);
                   var result= con.query('DELETE  FROM  schedule WHERE user_id=? and device_id=?',[user_id , device_id], (err,rows,fields)=>{
                    if(err){
                            // console.log('[MySql ERROR]',err);
                          
                            res.json('Schedule error: ');
                            console.log('Schedule Error');
                        
                        }else{
                            
                            console.log('Schedule deleted   Successfully');
                        }
                        
                    });
                });               



//here to fetch schedules , user send user_id and related data or schedules will be fitch
var user_id_for_schedule=0;
app.post('/User_ID_To_Fetch_Schedules_DB_API',(req,res,next)=>{
    console.log('User_ID_To_Fetch_Schedules_DB_API');        
    console.log('User_id_have_been_sent');
            var post_data = req.body;//get POST params
            user_id_for_schedule = post_data.user_id;
            console.log(user_id_for_schedule);
    });

    
// here related schedules fetch data will be send to  app
app.get('/Fetched_schedules_According_to_User_ID_API', (req, res)=>{
    console.log('Fetched_schedules_According_to_User_ID_API');  

    console.log ('user_id_for_schedule          post_get_user_id ' + post_get_user_id     );
    con.query('SELECT schedule_id,device_id,schedule_name,time_before,time_after,schedule_action , schedule_status FROM schedule where user_id=? AND schedule_status=1',[post_get_user_id],(err,rows,fields)=>{
            if (!err){
          
                console.log(rows);
                res.send(rows);
            } else 
                console.log(err);
        })
    })

    app.get('/Connection_Checker_API', (req, res)=>{
        
          var myObj = { error: ' Connected ' };
                console.log(myObj);
                res.send(myObj);
            
       
    });

    var password_update_status= 0 ; 
app.post('/POST_Update_new_Password_API',(req,res,next)=>{
    
            var post_data = req.body;
    //extract email and password from request
    var user_id = post_data.user_id;
    var  user_password= post_data.user_password;
    var query_string= "UPDATE user SET user_password =" + user_password + " WHERE user_id ="+ user_id;
        con.query(query_string, (err,rows,fields)=>{

            if (!err){
                password_update_status=1;
                console.log("Updated Password");
                res.send(rows);
            } else 
                console.log(err);
        });
    });
    app.get('/GET_Password_Update_Status_API', (req, res)=>{
        
        var myObj = { error: ' Update ' };
                console.log(myObj);
                res.send(myObj);
  });

  // this api is to update the profile phone number and name 
  app.post('/POST_Update_new_Ph_Name_Profile_API/',(req,res,next)=>{
    
    var post_data = req.body;
    //extract email and password from request
    var user_id = post_data.user_id;
    
        var user_name= post_data.user_name;
        var user_contact= post_data.user_contact;
        console.log("data is  " + post_data.user_name+user_contact+user_id);
        
        var query_string= "UPDATE user SET user_name ='" + user_name + "', user_contact='"+ user_contact +"' WHERE user_id ="+ user_id;
        con.query(query_string, (err,rows,fields)=>{

            if (!err){
                password_update_status=1;
                console.log("User name and Phone number updated ");
                res.send(rows);
            } else 
                console.log(err);
        });
});


////  Get  Data of devices from SQL on basis of User 
app.get('/GET_User_Device_Control_detail_on_basis_of_user_id', (req, res)=>{
   
    console.log('GET_User_Device_Control_detail_on_basis_of_user_id......user_id===='+post_get_user_id );
    con.query('SELECT device_id, device_name, device_status, device_on_off_status, device_on_command, device_off_command from device where  user_id=?',[post_get_user_id],(err,rows,fields)=>{
        if (!err){
      
            console.log(rows);
            res.send(rows);
        } else 
            console.log(err);
    })
});


var post_get_user_id=2;
////  Get  Data of Temperature_Humidity Table  from SQL on basis of User 
app.get('/GET_User_Temperature_Humidity_detail_on_basis_of_user_id', (req, res)=>{
   
    console.log('GET_User_Temperature_Humidity_detail_on_basis_of_user_id......user_id===='+post_get_user_id );
    
    con.query('SELECT th_id,user_id,temperature_value,humidity_value,data_added_time FROM temperature_humidity_table where fetched_data=0 and  user_id=?',[post_get_user_id],(err,rows,fields)=>{
        if (!err){
            con.query('update temperature_humidity_table set fetched_data=1 where user_id='+post_get_user_id);
            console.log(rows);
            res.send(rows);
        } else 
            console.log(err);
    })
});
///////// To set all Data enables for future use
app.post('/POST_User_ID_to_set_Fetched_data_Enable_Temperature_Humidity_detail/',(req,res,next)=>{
    
    var post_data = req.body;
    var user_id = post_data.user_id;
    
        var query_string= "'update temperature_humidity_table set fetched_data=0 where user_id="+ 2;
            con.query(query_string, (err,rows,fields)=>{

            if (!err){
                password_update_status=1;
                console.log("All Data set Enable ");
                res.send(rows);
            } else 
                console.log(err);
        });
})
//start server or port number 3000
app.listen(3000,()=>{
    console.log('Smart Building System Restful API running on port 3000');
})
