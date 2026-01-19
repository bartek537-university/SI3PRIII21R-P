const BASE_URL = location.origin;
const API_URL = `${BASE_URL}/api`;

const SHARE_TOKENS_LS_KEY = "share-tokens";

function getStoredTokens() {
    const shareTokensText = localStorage.getItem(SHARE_TOKENS_LS_KEY);
    return JSON.parse(shareTokensText);
}

async function fetchShare(shareId) {
    const shareEndpoint = `${API_URL}/shares/${shareId}`;

    const response = await fetch(shareEndpoint);
    const body = await response.json();

    if (!response.ok) {
        throw new Error(body);
    }

    return {...body, expiresAt: new Date(body.expiresAt)};
}

async function fetchAllShares(shareIds) {
    const results = await Promise.allSettled(shareIds.map(fetchShare));
    return results.filter(it => it.status === "fulfilled").map(it => it.value);
}

function saveStoredTokens(tokens) {
    const shareTokens = JSON.stringify(tokens);
    localStorage.setItem(SHARE_TOKENS_LS_KEY, shareTokens);
}

function pruneInvalidTokens(shares) {
    const allTokens = getStoredTokens();

    const shareIds = new Set(shares.map(it => it.id));
    const validTokens = allTokens.filter(it => shareIds.has(it.id));

    saveStoredTokens(validTokens);
}

async function changeExpiryDate(share, token) {
    const enteredDateText = prompt("Enter the expiry date");

    if (enteredDateText == null) {
        return;
    }

    const date = new Date(enteredDateText);

    if (isNaN(date.getTime())) {
        alert("The date you entered is invalid.");
        return;
    }

    const patchShareEndpoint = `${API_URL}/shares/${share.id}`;

    const response = await fetch(patchShareEndpoint, {
        method: "PATCH", headers: {
            "content-type": "application/json", "Management-Token": token,
        }, body: JSON.stringify({
            expiresAt: date.toISOString(),
        })
    })

    const body = await response.json();

    if (response.ok) {
        location.reload();
        return;
    }

    console.error("Failed to change expiry date.", body);

    if (response.status === 400) {
        alert("Failed to change the expiry date. It must be within a 24 hours after creation.");
    } else {
        alert(`Something went wrong... ${body.message}`);
    }
}

async function shareUpload(share) {
    const shareUrl = `${BASE_URL}/${share.slug}`;
    await navigator.clipboard.writeText(shareUrl);
}

async function deleteUpload(share, token) {
    if (!confirm("Are you sure you want to delete this share?")) {
        return;
    }

    const deleteShareEndpoint = `${API_URL}/shares/${share.id}`;

    const response = await fetch(deleteShareEndpoint, {
        method: "DELETE", headers: {"Management-Token": token}
    })

    if (response.ok) {
        location.reload();
        return;
    }

    const body = await response.json();

    console.error("Failed to delete share.", body);
    alert(`Something went wrong... ${body.message}`);
}

const attachmentItemTemplateElement = document.querySelector("#attachment-template");

function createAttachmentItemNode(attachment, share) {
    const attachmentDownloadEndpoint = `${API_URL}/shares/${share.id}/attachments/${attachment.id}/file`;

    const item = attachmentItemTemplateElement.content.cloneNode(true);

    const fileLinkElement = item.querySelector(".file-link");

    fileLinkElement.textContent = attachment.fileName;
    fileLinkElement.href = attachmentDownloadEndpoint;

    return item;
}

const uploadTableRowTemplateElement = document.querySelector("#upload-template");

function createShareRowNode(share, token) {
    const row = uploadTableRowTemplateElement.content.cloneNode(true);

    const slugElement = row.querySelector(".slug");
    const expiryDateElement = row.querySelector(".expiry-date")
    const dateButton = row.querySelector(".date-button");
    const attachmentListElement = row.querySelector(".attachments");
    const shareButton = row.querySelector(".share-button");
    const deleteButton = row.querySelector(".delete-button");

    slugElement.textContent = share.slug;
    expiryDateElement.dateTime = share.expiresAt.toISOString();
    expiryDateElement.textContent = share.expiresAt.toLocaleString();
    dateButton.addEventListener("click", async () => {
        await changeExpiryDate(share, token)
    })
    shareButton.addEventListener("click", async () => {
        await shareUpload(share)
    })
    deleteButton.addEventListener("click", async () => {
        await deleteUpload(share, token)
    })

    for (const attachment of share.attachments) {
        const attachmentEntry = createAttachmentItemNode(attachment, share);
        attachmentListElement.append(attachmentEntry);
    }

    return row;
}

const uploadTableBodyElement = document.querySelector("#uploads-table > tbody");

function displayShares(shares, tokens) {
    const tokenMap = Object.fromEntries(tokens.map(it => [it.id, it.token]));

    const items = shares.map(share => createShareRowNode(share, tokenMap[share.id]));
    items.forEach(item => uploadTableBodyElement.appendChild(item));
}

(async () => {
    const tokens = getStoredTokens();
    const shares = await fetchAllShares(tokens.map(entry => entry.id));

    displayShares(shares, tokens);

    pruneInvalidTokens(shares);
})()
