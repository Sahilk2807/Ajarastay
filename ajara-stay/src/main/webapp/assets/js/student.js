async function fetchJSON(url) {
  const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
  if (!res.ok) throw new Error('Request failed');
  return res.json();
}

async function init() {
  try {
    const [profile, room, fees, complaints, notices] = await Promise.all([
      fetchJSON('student?action=profile'),
      fetchJSON('student?action=room'),
      fetchJSON('student?action=fees'),
      fetchJSON('complaint?action=list'),
      fetchJSON('notice?action=list')
    ]);

    const profileEl = document.getElementById('profile');
    if (profileEl) profileEl.textContent = `${profile.name} • ${profile.email}`;

    const roomEl = document.getElementById('room');
    if (roomEl) roomEl.textContent = room && room.room_no ? `Room ${room.room_no}` : 'Not allocated';

    const feesEl = document.getElementById('fees');
    if (feesEl) feesEl.textContent = fees && fees.status ? `${String(fees.status).toUpperCase()} • Rs.${fees.amount ?? ''}` : 'No record';

    const list = document.getElementById('complaint-list');
    if (list) {
      list.innerHTML = '';
      (complaints || []).forEach(c => {
        const li = document.createElement('li');
        li.className = 'p-2 border rounded';
        li.textContent = `${c.complaint_text} — ${c.status}`;
        list.appendChild(li);
      });
    }

    const noticesEl = document.getElementById('notices');
    if (noticesEl) {
      noticesEl.innerHTML = '';
      (notices || []).forEach(n => {
        const li = document.createElement('li');
        li.className = 'p-2 border rounded bg-white';
        li.innerHTML = `<div class="font-medium">${n.title}</div><div class="text-gray-600">${n.description}</div>`;
        noticesEl.appendChild(li);
      });
    }
  } catch (e) {
    console.error(e);
  }
}

const form = document.getElementById('complaint-form');
if (form) {
  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    const formData = new FormData(form);
    const res = await fetch('complaint', { method: 'POST', body: formData });
    if (res.ok) init();
  });
}

init();

async function fetchJSON(url) {
  const res = await fetch(url, { headers: { Accept: application/json } });
  if (!res.ok) throw new Error(Request
