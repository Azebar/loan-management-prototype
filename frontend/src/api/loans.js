const BASE = "/api/loans";
async function jsonFetch(url, init) {
    const res = await fetch(url, {
        ...init,
        headers: {
            "Content-Type": "application/json",
            Accept: "application/json",
            ...(init?.headers ?? {}),
        },
    });
    if (!res.ok) {
        const text = await res.text();
        let message = res.statusText;
        try {
            const parsed = JSON.parse(text);
            if (parsed?.message)
                message = parsed.message;
        }
        catch {
            if (text)
                message = text;
        }
        throw new Error(`${res.status} ${message}`);
    }
    if (res.status === 204)
        return undefined;
    return (await res.json());
}
export const loansApi = {
    list: () => jsonFetch(BASE),
    get: (id) => jsonFetch(`${BASE}/${id}`),
    create: (input) => jsonFetch(BASE, { method: "POST", body: JSON.stringify(input) }),
    update: (id, input) => jsonFetch(`${BASE}/${id}`, { method: "PUT", body: JSON.stringify(input) }),
    remove: (id) => jsonFetch(`${BASE}/${id}`, { method: "DELETE" }),
    schedule: (id) => jsonFetch(`${BASE}/${id}/schedule`),
    scheduleCsvUrl: (id) => `${BASE}/${id}/schedule.csv`,
};
