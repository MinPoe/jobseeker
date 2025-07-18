function spawnJobCard() {
    const template = document.getElementById('job-card-template');
    const targetDiv = document.getElementById('items-container');

    const jobCard = template.content.cloneNode(true); 
    targetDiv.appendChild(jobCard);
}

document.getElementById('spawn-jobs').addEventListener('click', spawnJobCard); 