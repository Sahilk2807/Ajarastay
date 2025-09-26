async function fetchJSON(url) {
  const res = await fetch(url, { headers: { 'Accept': 'application/json' } });
  if (!res.ok) throw new Error('Request failed');
  return res.json();
}

async function loadStudents() {
  const students = await fetchJSON('admin?entity=student&action=list');
  const el = document.getElementById('students');
  if (!el) return;
  el.innerHTML = '';
  (students || []).forEach(s => {
    const li = document.createElement('li');
    li.className = 'p-2 border rounded bg-white flex justify-between items-center';
    li.innerHTML = `<span>#${s.student_id} • ${s.name} • ${s.fee_status}</span>
      <button data-id="${s.student_id}" class="text-xs px-2 py-1 bg-red-50 text-red-700 rounded delete-student">Delete</button>`;
    el.appendChild(li);
  });
}

async function loadRooms() {
  const rooms = await fetchJSON('admin?entity=room&action=list');
  const el = document.getElementById('rooms');
  if (!el) return;
  el.innerHTML = '';
  (rooms || []).forEach(r => {
    const li = document.createElement('li');
    li.className = 'p-2 border rounded bg-white';
    li.textContent = `Room ${r.room_no} • capacity ${r.capacity} • available ${r.available_beds}`;
    el.appendChild(li);
  });
}

async function loadComplaints() {
  const list = await fetchJSON('admin?entity=complaint&action=list');
  const el = document.getElementById('complaints');
  if (!el) return;
  el.innerHTML = '';
  (list || []).forEach(c => {
    const li = document.createElement('li');
    li.className = 'p-2 border rounded bg-white flex justify-between items-center';
    li.innerHTML = `<span>#${c.complaint_id} • ${c.complaint_text} — ${c.status}</span>
      <button data-id="${c.complaint_id}" class="text-xs px-2 py-1 bg-green-50 text-green-700 rounded resolve-complaint">Resolve</button>`;
    el.appendChild(li);
  });
}

function bindActions() {
  const refresh = document.getElementById('refresh-students');
  if (refresh) refresh.addEventListener('click', loadStudents);

  const roomForm = document.getElementById('room-form');
  if (roomForm) roomForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const fd = new FormData(roomForm);
    const res = await fetch('admin', { method: 'POST', body: fd });
    if (res.ok) loadRooms();
  });

  const feeForm = document.getElementById('fee-form');
  if (feeForm) feeForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const fd = new FormData(feeForm);
    await fetch('admin', { method: 'POST', body: fd });
    loadStudents();
  });

  document.addEventListener('click', async (e) => {
    const delBtn = e.target.closest && e.target.closest('.delete-student');
    if (delBtn) {
      const id = delBtn.getAttribute('data-id');
      const params = new URLSearchParams({ entity: 'student', action: 'delete', student_id: id });
      await fetch('admin', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: params });
      loadStudents();
    }
    const resBtn = e.target.closest && e.target.closest('.resolve-complaint');
    if (resBtn) {
      const id = resBtn.getAttribute('data-id');
      const params = new URLSearchParams({ entity: 'complaint', action: 'resolve', complaint_id: id });
      await fetch('admin', { method: 'POST', headers: { 'Content-Type': 'application/x-www-form-urlencoded' }, body: params });
      loadComplaints();
    }
  });
}

async function init() {
  await Promise.all([loadStudents(), loadRooms(), loadComplaints()]);
  bindActions();
}

init();

async function fetchJSON(url) {
  const res = await fetch(url, { headers: { Accept: application/json } });
  if (!res.ok) throw new Error(Request
