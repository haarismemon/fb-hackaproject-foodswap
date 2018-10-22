const Sequelize = require('sequelize');
const sequelize = require('./config');

const User = sequelize.define(
    'User', {
        //id: {type: Sequelize.INTEGER, autoIncrement:true},
        fname: {type:Sequelize.STRING(100), allowNull: false},
        lname:{type:Sequelize.STRING(50), allowNull: false},
        gender:{type: Sequelize.INTEGER, allowNull: false},
        //0-male, 1-female, 2-other, 3-prefer not to say
        nationality:{type:Sequelize.STRING(100), allowNull: false},
        dietary:{type:Sequelize.STRING(100)},
        pass:{type:Sequelize.STRING(100), allowNull: false},
        email:{type:Sequelize.STRING(100), allowNull: false, unique: true},
        dob:{type:Sequelize.DATE, allowNull: false}
        //'YYYY-MM-DD'
    },{
        freezeTableName: true
    }
);

User.sync({force: false})
    .then(function() {
        console.log("User sync successfully");
    })
    .catch(function(err){
        console.log("User sync fail: "+err);
});


module.exports = User;
