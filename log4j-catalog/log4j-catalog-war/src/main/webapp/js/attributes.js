/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache license, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the license for the specific language governing permissions and
 * limitations under the license.
 */
$(document).ready(function () {
    $('#AttributesTableContainer').jtable({
        title: 'Table of Attributes',
        paging: true, //Enable paging
        pageSize: 25, //Set page size (default: 25)
        sorting: true, //Enable sorting
        defaultSorting: 'Name ASC', //Set default sorting
        actions: {
            listAction: 'api/attributes/list',
        },
        toolbar: {
            items: [{
                icon: 'js/jtable.2.4.0/themes/metro/add.png',
                text: 'Add new record',
                click: () => { addEditAttributeItem() }
            }]
        },
        fields: {
            id: {
                key: true,
                list: false
            },
            name: {
                title: 'Name',
                width: '15%'
            },
            displayName: {
                title: 'Display Name',
                width: '15%'
            },
            description: {
                title: 'Description',
                width: '25%'
            },
            dataType: {
                title: 'Data Type',
                width: '5%'
            },
            indexed: {
                title: 'Indexed',
                width: '5%',
                display: function (attributeData) {
                    return attributeData.record.indexed ? 'true' : 'false';
                }
            },
            sortable: {
                title: 'Sortable',
                width: '5%',
                display: function (attributeData) {
                    return attributeData.record.sortable ? 'true' : 'false';
                }
            },
            required: {
                title: 'Required',
                width: '5%',
                display: function (attributeData) {
                    return attributeData.record.required ? 'true' : 'false';
                }
            },
            requestContext: {
                title: 'RequestContext',
                width: '3%',
                display: function (attributeData) {
                    return attributeData.record.requestContext ? 'true' : 'false';
                }
            },
            constraints: {
                title: 'Constraints',
                width: '16%',
                sorting: false,
                edit: false,
                create: false,
                display: function (attributeData) {
                    var constraintList = '';
                    if (typeof(attributeData.record.constraints) != 'undefined' && attributeData.record.constraints != null) {
                        constraintList = attributeData.record.constraints.map(function (elem) {
                            return elem.constraintType.name + '("' + elem.value + '")'
                        }).join(' | ');
                    }
                    //Create a div that will be used to view associated attributes
                    var $divConstraints = $('<div class="constraints">' + constraintList + '</div>');
                    return $divConstraints;
                }
            },
            edit: {
                title: '',
                width: '2%',
                display: function (attributeData) {
                    // Store attribute item data in localStorage
                    var attributeDataItem = JSON.stringify(attributeData.record);
                    localStorage.setItem('attributeItem' + attributeData.record.id,attributeDataItem);
                    return '<img class="log4J-action-icon" src="js/jtable.2.4.0/themes/metro/edit.png" onClick="addEditAttributeItem(' + attributeData.record.id + ')" />';
                }
            },
            delete: {
                title: '',
                width: '2%',
                display: function (attributeData) {
                    return '<img class="log4J-action-icon" src="js/jtable.2.4.0/themes/metro/delete.png" onClick="deleteAttributeItem(' + attributeData.record.id + ')" />';
                }
            }
        }
    });
    $.ajax({
        type: 'GET',
        url: 'api/constraints/types',
        success:function(response){
            localStorage.setItem('allConstraints', response);
        },
        error:function(jqXhr, textStatus, errorThrown){
            console.error(textStatus + ' - ' + errorThrown);
        }
    });
    //Load attributes list from server
    $('#AttributesTableContainer').jtable('load');
});

function deleteAttributeItem(attributeId) {
    var response = confirm('Are you sure you want to delete this attribute?');
    if (response) {
        var postData = {};
        postData['id'] = attributeId
        $.ajax({
            type: 'POST',
            contentType: 'application/json',
            url: 'api/attributes/delete',
            data: JSON.stringify(postData),
            success:function(response) {
                if (response.Result === 'OK') {
                    $('#AttributesTableContainer').jtable('load');
                }
            },
            error:function(jqXhr, textStatus, errorThrown) {
                console.error(textStatus + ' - ' + errorThrown);
            }
        });
    }
}

function addEditattributeItemHandler() {
  var validForm = validateFormContent();
  if (validForm) {
      showLoadingAnimation();
      var postUrl = 'api/attributes/create';
      var postData = {};
      var attributeConstraints = [];
      postData['name'] = $('#attributeName').val();
      postData['displayName'] = $('#attributeDisplayName').val();
      postData['description'] = $('#attributeDescription').val();
      postData['dataType'] = $('#attributeDataType').val();
      postData['indexed'] = $('#attributeIndexed').val();
      postData['sortable'] = $('#attributeSortable').val();
      postData['required'] = $('#attributeRequired').val();
      postData['requestContext'] = $('#attributeRequestContext').val();
      $('#attributeConstraints .attribute-constraint-row').each(function() {
          var attributeConstraintItem = {
              constraintType: { name: $(this).find('.attribute-constraint-name').text().toLowerCase() },
              value: $(this).find('input')[0].value,
          };
          attributeConstraints.push(attributeConstraintItem);
      });
      postData['constraints'] = attributeConstraints;
      if ($('#attributeId').val()) {
          postUrl = 'api/attributes/update';
          postData['id'] = $('#attributeId').val();
      }
      $.ajax({
          type: 'POST',
          contentType: 'application/json',
          url: postUrl,
          data: JSON.stringify(postData),
          success:function(response) {
              if (response.Result === 'OK') {
                  $('#AttributesTableContainer').jtable('load');
                  closeLog4jModal();
              }
          },
          error:function(jqXhr, textStatus, errorThrown) {
              console.error(textStatus + ' - ' + errorThrown);
          }
      });
  }
}

function addEditAttributeItem(attributeId) {
  var hiddenIdField = '';
  var attributeData = {};
  if (attributeId) {
      hiddenIdField = '<input type="hidden" id="attributeId" name="id" value="' + attributeId + '" />';
  } else {
      attributeId = 'tempEventData';
      var tempEventData = {
          id: attributeId,
          constraints: [],
      }
      localStorage.setItem('attributeItem' + attributeId, JSON.stringify(tempEventData));
  }
  attributeFormContent = ' \
      <form id="add-edit-attribute-form" class="log4j-catalog-form" method="post"> \
        ' + hiddenIdField + ' \
        <p> \
            <label>Name</label> \
            <input type="text" id="attributeName" name="name" class="required" /> \
        </p> \
        <p> \
            <label>Display Name</label> \
            <input type="text" id="attributeDisplayName" name="displayName" class="required" /> \
        </p> \
        <p> \
            <label>Description</label> \
            <input type="text" id="attributeDescription" name="description" class="required" /> \
        </p> \
        <p> \
            <label>Data Type</label> \
            <select id="attributeDataType" name="indexed" class="required"> \
            </select> \
        </p> \
        <p> \
            <label>Indexed</label> \
            <select id="attributeIndexed" name="indexed" class="required"> \
                <option value="false">false</option> \
                <option value="true">true</option> \
            </select> \
        </p> \
        <p> \
            <label>Sortable</label> \
            <select id="attributeSortable" name="sortable" class="required"> \
                <option value="false">false</option> \
                <option value="true">true</option> \
            </select> \
        </p> \
        <p> \
            <label>Required</label> \
            <select id="attributeRequired" name="required" class="required"> \
                <option value="false">false</option> \
                <option value="true">true</option> \
            </select> \
        </p> \
        <p> \
            <label>Request Context</label> \
            <select id="attributeRequestContext" name="requestContext" class="required"> \
                <option value="false">false</option> \
                <option value="true">true</option> \
            </select> \
        </p> \
        <p> \
            <label>Assigned Constraints</label> \
            <span id="attributeConstraints"></span> \
        </p> \
        <p> \
            <label>Add Constraint</label> \
            <span> \
                <select name="addAttributeConstraintName" id="addAttributeConstraintName"> \
                    <option value="">loading...</option> \
                </select> \
                <input type="text" name="addAttributeConstraintValue" id="addAttributeConstraintValue" /> \
                <button id="addAttributeConstraintButton">+</button> \
            </span> \
        </p> \
      </form> \
      <div class="log4j-catalog-button-row"> \
          <button class="log4j-catalog-button" onclick="closeLog4jModal()">Cancel</button>\
          <button class="log4j-catalog-button" onclick="addEditattributeItemHandler()">Save</button> \
      </div> \
  ';
  showLog4JModal('Add / Edit Attribute Item', attributeFormContent);
  var dataTypes = ['STRING', 'BIG_DECIMAL', 'DOUBLE', 'FLOAT', 'INT', 'LONG', 'BOOLEAN', 'LIST', 'MAP'];
  $.each(dataTypes.sort(), function(index, value) {
      $('#attributeDataType').append('<option value="' + value + '">' + value + '</option>');
  });
  if (localStorage.getItem('attributeItem' + attributeId)) {
      attributeData = JSON.parse(localStorage.getItem('attributeItem' + attributeId));
      $('#attributeName').val(attributeData.name);
      $('#attributeDisplayName').val(attributeData.displayName);
      $('#attributeDescription').val(attributeData.description);
      $('#attributeDataType option[value="' + attributeData.dataType + '"]').attr('selected', 'selected');
      $('#attributeIndexed option[value="' + attributeData.indexed + '"]').attr('selected', 'selected');
      $('#attributeSortable option[value="' + attributeData.sortable + '"]').attr('selected', 'selected');
      $('#attributeRequired option[value="' + attributeData.required + '"]').attr('selected', 'selected');
      $('#attributeRequestContext option[value="' + attributeData.requestContext + '"]').attr('selected', 'selected');
  }
  populateAttributeConstraints(attributeData.constraints, attributeId);
}

function populateAttributeConstraints(assignedConstraints, attributeId) {
    var selectedConstraints = [];
    $('#attributeConstraints').children().remove();
    if (attributeId && assignedConstraints) {
        assignedConstraints.map((item) => {
            selectedConstraints.push(item.constraintType.name);
            $('#attributeConstraints').append(' \
                <span class="attribute-constraint-row"> \
                    <span class="attribute-constraint-name">' + item.constraintType.name + '</span> \
                    <input type="text" name="constraints[]" class="attribute-constraint-data required" value="' + item.value + '" /> \
                    <button class="remove-attribute-constraint-button" alt="' + attributeId + '" rel="' + item.constraintType.name + '">-</button> \
                </span> \
            ');
        });
    }
    function checkPendingRequest() {
        if ($.active > 0) {
            window.setTimeout(checkPendingRequest, 1000);
        } else {
            var allConstraints = localStorage.getItem('allConstraints').split(',');
            allConstraints.sort();
            $('#addAttributeConstraintName option').remove();
            allConstraints.map((item) => {
                if (!selectedConstraints.includes(item)) {
                    $('#addAttributeConstraintName').append(' \
                        <option value="' + item + '">' + item.toUpperCase() + '</option> \
                    ');
                }
            });
        }
    }
    checkPendingRequest();
    assignAttributeConstraintListeners(attributeId);
}

function assignAttributeConstraintListeners(attributeId) {
    $('#addAttributeConstraintButton, .remove-attribute-constraint-button').unbind();
    $('#addAttributeConstraintButton').click(function(e) {
        e.preventDefault();
        var allConstraints = localStorage.getItem('allConstraints').split(',');
        attributeData = JSON.parse(localStorage.getItem('attributeItem' + attributeId));
        if (typeof(attributeData.constraints) == 'undefined' || attributeData.constraints == null) {
            attributeData.constraints = [];
        }
        attributeData.constraints.push({
            constraintType: { name: $('#addAttributeConstraintName').val() },
            value: $('#addAttributeConstraintValue').val()
        });
        localStorage.setItem('attributeItem' + attributeId, JSON.stringify(attributeData));
        $('#addAttributeConstraintValue').val('');
        populateAttributeConstraints(attributeData.constraints, attributeId);
    });
    $('.remove-attribute-constraint-button').click(function(e) {
        e.preventDefault();
        var allConstraints = localStorage.getItem('allConstraints').split(',');
        attributeData = JSON.parse(localStorage.getItem('attributeItem' + attributeId));
        var newConstraints = attributeData.constraints.filter((obj) => {
            return obj.constraintType.name !== $(this).attr('rel');
        });
        attributeData['constraints'] = newConstraints;
        localStorage.setItem('attributeItem' + attributeId, JSON.stringify(attributeData));
        populateAttributeConstraints(attributeData.constraints, attributeId);
    });
}
