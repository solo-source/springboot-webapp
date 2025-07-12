document.addEventListener('DOMContentLoaded', function() {
    const loginTab = document.getElementById('loginTab');
    const registerTab = document.getElementById('registerTab');
    const loginFormSection = document.getElementById('loginFormSection');
    const registerFormSection = document.getElementById('registerFormSection');

    function showTab(tabName) {
        if (tabName === 'login') {
            loginTab.classList.add('active');
            registerTab.classList.remove('active');
            loginFormSection.style.display = 'block';
            registerFormSection.style.display = 'none';
        } else if (tabName === 'register') {
            registerTab.classList.add('active');
            loginTab.classList.remove('active');
            registerFormSection.style.display = 'block';
            loginFormSection.style.display = 'none';
        }
    }

    loginTab.addEventListener('click', function() {
        showTab('login');
    });

    registerTab.addEventListener('click', function() {
        showTab('register');
    });

    // Initial tab display is handled by Thymeleaf in the HTML for server-side
    // rendered error/success states, then JS takes over for user interaction.
});