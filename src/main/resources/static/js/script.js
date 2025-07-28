document.addEventListener("DOMContentLoaded", function () {

  // ==== SIDEBAR TOGGLE FUNCTION ====
  const toggleSidebar = () => {
    if ($('.sidebar').is(":visible")) {
      $(".sidebar").css("display", "none");
      $(".content").css("margin-left", "0%");
      $(".sidebar-overlay").css("display", "none");
      $("body").removeClass("sidebar-active");
    } else {
      $(".sidebar").css("display", "block");
      if ($(window).width() <= 600) {
        $(".sidebar-overlay").css("display", "block");
        $("body").addClass("sidebar-active");
      } else {
        $(".content").css("margin-left", "20%");
      }
    }
  };

  // Make the function accessible if needed from HTML
  window.toggleSidebar = toggleSidebar;

  // ==== LIVE SEARCH FUNCTION ====
  const searchInput = document.querySelector('.search-box-inner input');
  const resultBox = document.querySelector('.search-result');

  if (searchInput && resultBox) {
    resultBox.style.display = 'none';

    searchInput.addEventListener('input', async function () {
      const query = searchInput.value.trim();

      if (query.length > 0) {
        try {
          const response = await fetch(`/user/search/${query}`);
          const data = await response.json();

          resultBox.style.display = 'block';
          resultBox.innerHTML = '';

          if (data.length === 0) {
            resultBox.innerHTML = '<p>No contacts found.</p>';
          } else {
            data.forEach(contact => {
              const contactId = parseInt(contact.cId); // ✅ safely parsed to integer
              const div = document.createElement('div');
              div.innerHTML = `
                <a href="/user/contact-profile/${contactId}" style="text-decoration: none; color: inherit;">
                  <div style="padding:5px; border-bottom:1px solid #ccc;">
                    <strong>${contact.name}</strong>
                  </div>
                </a>
              `;
              resultBox.appendChild(div);
            });
          }

        } catch (error) {
          resultBox.innerHTML = '<p>Error fetching results.</p>';
          resultBox.style.display = 'block';
        }
      } else {
        resultBox.style.display = 'none';
      }
    });
  }

});
