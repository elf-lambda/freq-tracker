let threadsChart, repliesChart, imagesChart;

function initCharts() {
    const chartConfig = {
        type: 'line',
        options: {
            responsive: true,
            maintainAspectRatio: false, // Allows the CSS height to take control
            plugins: {
                legend: {
                    display: true,
                    position: 'bottom', // Move legend to bottom to save horizontal space
                    labels: {
                        color: '#fff',
                        boxWidth: 12,
                        font: { size: 11 }
                    }
                }
            },
            scales: {
                x: {
                    ticks: {
                        color: '#999',
                        maxRotation: 0,
                        font: { size: 10 }
                    },
                    grid: { color: '#333' }
                },
                y: {
                    ticks: {
                        color: '#999',
                        font: { size: 10 }
                    },
                    grid: { color: '#333' },
                    beginAtZero: true
                }
            }
        }
    };

    threadsChart = new Chart(document.getElementById('threadsChart'), {
        ...chartConfig,
        data: {
            labels: [],
            datasets: [{
                label: 'New Threads',
                data: [],
                borderColor: '#4CAF50',
                backgroundColor: 'rgba(76, 175, 80, 0.1)',
                fill: true,
                tension: 0.4
            }]
        }
    });

    repliesChart = new Chart(document.getElementById('repliesChart'), {
        ...chartConfig,
        data: {
            labels: [],
            datasets: [{
                label: 'New Replies',
                data: [],
                borderColor: '#2196F3',
                backgroundColor: 'rgba(33, 150, 243, 0.1)',
                fill: true,
                tension: 0.4
            }]
        }
    });

    imagesChart = new Chart(document.getElementById('imagesChart'), {
        ...chartConfig,
        data: {
            labels: [],
            datasets: [{
                label: 'New Images',
                data: [],
                borderColor: '#FF9800',
                backgroundColor: 'rgba(255, 152, 0, 0.1)',
                fill: true,
                tension: 0.4
            }]
        }
    });
}

function updateCharts(data) {
    // Only show last 30 data points to keep it compact
    const limitedData = data.slice(-30);
    const labels = limitedData.map(d => d.timestamp);
    const threads = limitedData.map(d => d.newThreads);
    const replies = limitedData.map(d => d.replies);
    const images = limitedData.map(d => d.images);

    threadsChart.data.labels = labels;
    threadsChart.data.datasets[0].data = threads;
    threadsChart.update('none');

    repliesChart.data.labels = labels;
    repliesChart.data.datasets[0].data = replies;
    repliesChart.update('none');

    imagesChart.data.labels = labels;
    imagesChart.data.datasets[0].data = images;
    imagesChart.update('none');
}

function updateStats(stats) {
    document.getElementById('current-threads').textContent = stats.currentThreads;
    document.getElementById('current-replies').textContent = stats.currentReplies;
    document.getElementById('current-images').textContent = stats.currentImages;
    document.getElementById('total-data').textContent = stats.totalDataPoints;
}

async function fetchData() {
    try {
        const [metricsResponse, statsResponse] = await Promise.all([
            fetch('/api/metrics'),
            fetch('/api/stats')
        ]);

        const metrics = await metricsResponse.json();
        const stats = await statsResponse.json();

        updateCharts(metrics);
        updateStats(stats);
    } catch (error) {
        console.error('Error fetching data:', error);
    }
}

initCharts();
fetchData();

setInterval(fetchData, 5000);