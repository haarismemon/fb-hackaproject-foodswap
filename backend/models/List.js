const Sequelize = require('sequelize');
const sequelize = require('./config');

const List = sequelize.define(
    'List', {
        //id: {type: Sequelize.INTEGER, autoIncrement:true},
        uid: {type:Sequelize.INTEGER, allowNull: false},
        //the food that user wants to cook
        food:{type:Sequelize.STRING(50), allowNull: false},
        //0-pending, 1-confirm, 2-done
        status:{type: Sequelize.INTEGER, allowNull: false},
        //'YYYY-MM-DD'
        date:{type:Sequelize.DATE, allowNull: false},
        partnerid: {type:Sequelize.INTEGER, allowNull: true}
        
    },{
        freezeTableName: true
    }
);

List.sync({force: false})
    .then(function() {
        console.log("List sync successfully");
    })
    .catch(function(err){
        console.log("List sync fail: "+err);
});


module.exports = List;