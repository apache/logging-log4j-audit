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
    // Clear localStorage
    localStorage.clear();

    $('#save-all').click(function(e) {
      e.preventDefault();
      saveAllChanges();
    });
});

// Modal action handlers
function closeLog4jModal() {
    $('.log4j-catalog-modal').remove();
}

function log4jSubmitHandler(submitHandler) {
    submitHandler();
    closeLog4jModal();
}

function showLog4JModal(title, content) {
    closeLog4jModal();
    var modalContent = ' \
        <div class="log4j-catalog-modal"> \
            <div class="log4j-catalog-title">' + title + '</div> \
            <div class="log4j-catalog-content">' + content + '</div> \
        </div>';

    $('body').append(modalContent);
    window.scrollTo(0, 0);
}

function showLoadingAnimation() {
  $('.log4j-catalog-form').prepend('<div class="form-processing"><div class="gif"></div></div>');
}

function validateFormContent() {
    var errors = 0;
    $('.form-error').remove();
    $('.required').each(function() {
        if (!$(this).val()) {
          errors++;
          $('<span class="form-error">Required.</span>').insertAfter($(this));
        }
    });
    if (errors) return false;
    return true;
}

function saveAllChanges() {
  $.ajax({
      type: 'POST',
      contentType: 'application/json',
      url: 'catalog',
      data: null,
      success:function(response) {
          if (response.Result === 'OK') {
              $('.log4j-table-container"').jtable('load');
          }
      },
      error:function(jqXhr, textStatus, errorThrown) {
          console.error(textStatus + ' - ' + errorThrown);
      }
  });
}
