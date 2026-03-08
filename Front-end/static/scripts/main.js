// AutoStock - Main JavaScript File

// DOM Elements
const mobileMenuBtn = document.getElementById('mobileMenuBtn');
const mobileMenu = document.getElementById('mobileMenu');
const contactForm = document.getElementById('contactForm');

// Mobile Menu Toggle
if (mobileMenuBtn && mobileMenu) {
    mobileMenuBtn.addEventListener('click', function() {
        mobileMenu.classList.toggle('active');
        mobileMenuBtn.classList.toggle('active');
    });

    // Close mobile menu when clicking on links
    const mobileLinks = document.querySelectorAll('.nav-mobile-link');
    mobileLinks.forEach(link => {
        link.addEventListener('click', function() {
            mobileMenu.classList.remove('active');
            mobileMenuBtn.classList.remove('active');
        });
    });

    // Close mobile menu when clicking outside
    document.addEventListener('click', function(event) {
        if (!mobileMenuBtn.contains(event.target) && !mobileMenu.contains(event.target)) {
            mobileMenu.classList.remove('active');
            mobileMenuBtn.classList.remove('active');
        }
    });
}

// Smooth Scrolling for Navigation Links
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            const headerHeight = document.querySelector('.header').offsetHeight;
            const targetPosition = target.offsetTop - headerHeight;
            
            window.scrollTo({
                top: targetPosition,
                behavior: 'smooth'
            });
        }
    });
});

// Contact Form Handling
if (contactForm) {
    contactForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        // Get form data
        const formData = new FormData(contactForm);
        const data = Object.fromEntries(formData);
        
        // Basic validation
        if (!data.phone || !data.storeName || !data.position || !data.vehicleQuantity) {
            showNotification('Por favor, preencha todos os campos obrigatórios.', 'error');
            return;
        }
        
        // Phone validation (basic Brazilian phone format)
        const phoneRegex = /^\(\d{2}\)\s\d{4,5}-\d{4}$/;
        if (!phoneRegex.test(data.phone)) {
            showNotification('Por favor, insira um telefone válido no formato (11) 99999-9999.', 'error');
            return;
        }
        
        // Simulate form submission
        showNotification('Solicitação enviada com sucesso! Entraremos em contato em breve.', 'success');
        contactForm.reset();
        
        // In a real application, you would send the data to your server here
        console.log('Form data:', data);
    });
}

// Phone Number Formatting
const phoneInput = document.getElementById('phone');
if (phoneInput) {
    phoneInput.addEventListener('input', function(e) {
        let value = e.target.value.replace(/\D/g, '');
        
        if (value.length <= 11) {
            if (value.length <= 2) {
                value = value.replace(/(\d{0,2})/, '($1');
            } else if (value.length <= 6) {
                value = value.replace(/(\d{2})(\d{0,4})/, '($1) $2');
            } else if (value.length <= 10) {
                value = value.replace(/(\d{2})(\d{4})(\d{0,4})/, '($1) $2-$3');
            } else {
                value = value.replace(/(\d{2})(\d{5})(\d{0,4})/, '($1) $2-$3');
            }
        }
        
        e.target.value = value;
    });
}

// Header Background on Scroll
window.addEventListener('scroll', function() {
    const header = document.querySelector('.header');
    if (header) {
        if (window.scrollY > 100) {
            header.style.backgroundColor = 'rgba(255, 255, 255, 0.98)';
        } else {
            header.style.backgroundColor = 'rgba(255, 255, 255, 0.95)';
        }
    }
});

// Intersection Observer for Animations
const observerOptions = {
    threshold: 0.1,
    rootMargin: '0px 0px -50px 0px'
};

const observer = new IntersectionObserver(function(entries) {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.classList.add('animate-in');
        }
    });
}, observerOptions);

// Observe elements for animation
document.addEventListener('DOMContentLoaded', function() {
    const animateElements = document.querySelectorAll('.card, .solution-card, .feature-card');
    animateElements.forEach(el => {
        observer.observe(el);
    });
});

// Notification System
function showNotification(message, type = 'info') {
    // Remove existing notifications
    const existingNotifications = document.querySelectorAll('.notification');
    existingNotifications.forEach(notification => notification.remove());
    
    // Create notification element
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.innerHTML = `
        <div class="notification-content">
            <span class="notification-message">${message}</span>
            <button class="notification-close" onclick="this.parentElement.parentElement.remove()">×</button>
        </div>
    `;
    
    // Add styles
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        z-index: 1000;
        max-width: 400px;
        padding: 1rem;
        border-radius: 0.5rem;
        box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
        transform: translateX(100%);
        transition: transform 0.3s ease;
        ${type === 'success' ? 'background-color: #10b981; color: white;' : ''}
        ${type === 'error' ? 'background-color: #ef4444; color: white;' : ''}
        ${type === 'info' ? 'background-color: #3b82f6; color: white;' : ''}
    `;
    
    notification.querySelector('.notification-content').style.cssText = `
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 1rem;
    `;
    
    notification.querySelector('.notification-close').style.cssText = `
        background: none;
        border: none;
        color: inherit;
        font-size: 1.5rem;
        cursor: pointer;
        padding: 0;
        line-height: 1;
    `;
    
    // Add to DOM
    document.body.appendChild(notification);
    
    // Animate in
    setTimeout(() => {
        notification.style.transform = 'translateX(0)';
    }, 100);
    
    // Auto remove after 5 seconds
    setTimeout(() => {
        if (notification.parentElement) {
            notification.style.transform = 'translateX(100%)';
            setTimeout(() => {
                notification.remove();
            }, 300);
        }
    }, 5000);
}

// Add animation styles
const animationStyles = document.createElement('style');
animationStyles.textContent = `
    .card, .solution-card, .feature-card {
        opacity: 0;
        transform: translateY(20px);
        transition: opacity 0.6s ease, transform 0.6s ease;
    }
    
    .card.animate-in, .solution-card.animate-in, .feature-card.animate-in {
        opacity: 1;
        transform: translateY(0);
    }
    
    .solution-card:nth-child(1).animate-in { transition-delay: 0.1s; }
    .solution-card:nth-child(2).animate-in { transition-delay: 0.2s; }
    .solution-card:nth-child(3).animate-in { transition-delay: 0.3s; }
    .solution-card:nth-child(4).animate-in { transition-delay: 0.4s; }
    .solution-card:nth-child(5).animate-in { transition-delay: 0.5s; }
    .solution-card:nth-child(6).animate-in { transition-delay: 0.6s; }
    
    .feature-card:nth-child(1).animate-in { transition-delay: 0.1s; }
    .feature-card:nth-child(2).animate-in { transition-delay: 0.2s; }
    .feature-card:nth-child(3).animate-in { transition-delay: 0.3s; }
    .feature-card:nth-child(4).animate-in { transition-delay: 0.4s; }
`;

document.head.appendChild(animationStyles);

// Performance optimization: Lazy load images
const lazyImages = document.querySelectorAll('img[data-src]');
const imageObserver = new IntersectionObserver((entries, observer) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            const img = entry.target;
            img.src = img.dataset.src;
            img.classList.remove('lazy');
            imageObserver.unobserve(img);
        }
    });
});

lazyImages.forEach(img => imageObserver.observe(img));

// Initialize everything when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    console.log('AutoStock website loaded successfully!');
    
    // Add loaded class to body for any CSS transitions
    document.body.classList.add('loaded');
});