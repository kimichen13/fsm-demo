import Alpine from "alpinejs"

window.Alpine = Alpine

window.eventData = function () {
    return {
        title: 'Events',
        events: [],
        selectedEvent: '',
        initEvent() {
            fetch("/fsm/events")
                .then(response => response.json())
                .then(data => this.events = data)
            this.$watch('selectedEvent', value => console.log(value));
        }
    }
};

Alpine.start()