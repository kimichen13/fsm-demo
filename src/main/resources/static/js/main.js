document.addEventListener('alpine:init', () => {
    fetch('/fsm/states', {
        method: 'GET',
    })
        .then((result) => result.json())
        .then(data => {
            console.log(data)
            this.states = data
        })
        .catch((error) => {
            console.error('Error:', error);
        });
    Alpine.data("states", this.states)
});