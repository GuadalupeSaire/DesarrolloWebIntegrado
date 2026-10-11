document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.table-wrapper').forEach((wrapper) => {
        const table = wrapper.querySelector('table');
        if (!table) return;

        const topScroll = document.createElement('div');
        const topScrollContent = document.createElement('div');
        topScroll.className = 'table-scroll-top';
        topScroll.setAttribute('role', 'region');
        topScroll.setAttribute('aria-label', 'Desplazamiento horizontal de la tabla');
        topScroll.tabIndex = 0;
        topScroll.appendChild(topScrollContent);
        wrapper.parentNode.insertBefore(topScroll, wrapper);

        let sincronizando = false;
        const sincronizarAncho = () => {
            topScrollContent.style.width = `${table.scrollWidth}px`;
            topScroll.hidden = table.scrollWidth <= wrapper.clientWidth;
        };
        const sincronizarDesplazamiento = (origen, destino) => {
            if (sincronizando) return;
            sincronizando = true;
            destino.scrollLeft = origen.scrollLeft;
            sincronizando = false;
        };

        topScroll.addEventListener('scroll', () => sincronizarDesplazamiento(topScroll, wrapper));
        wrapper.addEventListener('scroll', () => sincronizarDesplazamiento(wrapper, topScroll));
        window.addEventListener('resize', sincronizarAncho);
        sincronizarAncho();

        if ('ResizeObserver' in window) {
            const observer = new ResizeObserver(sincronizarAncho);
            observer.observe(wrapper);
            observer.observe(table);
        }
    });
});
