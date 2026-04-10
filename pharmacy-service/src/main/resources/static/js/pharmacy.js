const API = "http://localhost:8084";

async function loadMedicines() {
	const res = await fetch(`${API}/medicines`);
	const data = await res.json();

	const table = document.getElementById("medicineTable");
	table.innerHTML = "";

	data.forEach(m => {
		table.innerHTML += `
            <tr>
                <td>${m.id}</td>
                <td>${m.name}</td>
                <td>${m.pricePerUnit}</td>
                <td>${m.stockQuantity}</td>
            </tr>
        `;
	});
}

async function searchMedicines() {
	const name = document.getElementById("searchBox").value;

	const res = await fetch(`${API}/medicines/search?name=${name}`);
	const data = await res.json();

	const table = document.getElementById("medicineTable");
	table.innerHTML = "";

	data.forEach(m => {
		table.innerHTML += `
            <tr>
                <td>${m.id}</td>
                <td>${m.name}</td>
                <td>${m.pricePerUnit ?? "-"}</td>
                <td>${m.stockQuantity ?? "-"}</td>
            </tr>
        `;
	});
}